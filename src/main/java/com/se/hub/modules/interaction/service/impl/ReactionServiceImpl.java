package com.se.hub.modules.interaction.service.impl;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.blog.constant.BlogCacheConstants;
import com.se.hub.modules.blog.repository.BlogRepository;
import com.se.hub.modules.interaction.dto.response.ReactionDetailResponse;
import com.se.hub.modules.interaction.dto.response.ReactionInfo;
import com.se.hub.modules.interaction.dto.response.ReactionResponse;
import com.se.hub.modules.interaction.dto.response.ReactionToggleResult;
import com.se.hub.modules.interaction.entity.Comment;
import com.se.hub.modules.interaction.entity.Reaction;
import com.se.hub.modules.interaction.enums.ReactionType;
import com.se.hub.modules.interaction.enums.TargetType;
import com.se.hub.modules.interaction.mapper.ReactionMapper;
import com.se.hub.modules.interaction.repository.CommentRepository;
import com.se.hub.modules.interaction.repository.ReactionRepository;
import com.se.hub.modules.interaction.service.api.ReactionService;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.profile.repository.ProfileRepository;
import com.se.hub.modules.profile.repository.UserStatsRepository;
import com.se.hub.modules.profile.service.api.ActivityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Reaction Service Implementation
 * Virtual Thread Best Practice:
 * - This service uses synchronous blocking I/O operations (JPA repository calls)
 * - Virtual threads automatically handle blocking operations efficiently
 * - No need to use CompletableFuture or reactive APIs
 * - Each method call will run on a virtual thread, allowing high concurrency
 * - Database operations are blocking but virtual threads handle them efficiently
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReactionServiceImpl implements ReactionService {

    ReactionRepository reactionRepository;
    CommentRepository commentRepository;
    BlogRepository blogRepository;
    ProfileRepository profileRepository;
    UserStatsRepository userStatsRepository;
    ActivityService activityService;
    ReactionMapper reactionMapper;

    /**
     * Toggle reaction (like/unlike) for a target.
     * Virtual Thread Best Practice: Uses @Transactional with synchronous blocking I/O operations.
     * Virtual threads yield during each database operation, enabling high concurrency.
     * Race conditions are handled using unique constraint with exception handling.
     */
    @Override
    @Transactional
    public boolean toggleReaction(TargetType targetType, String targetId, ReactionType reactionType) {

        Profile currentUser = getCurrentUser();

        Optional<Reaction> existing = reactionRepository
                .findByTargetTypeAndTargetIdAndUser(targetType, targetId, currentUser);

        return existing
                .map(reaction -> handleExistingReaction(reaction, reactionType, targetType, targetId))
                .orElseGet(() -> handleNewReaction(targetType, targetId, reactionType, currentUser));

    }

    /* ========================  HELPERS  ======================== */

    private Profile getCurrentUser() {
        String userId = AuthUtils.getCurrentUserId();
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
    }

    /**
     * Existing reaction → remove or update
     */
    private boolean handleExistingReaction(
            Reaction reaction, ReactionType newType,
            TargetType targetType, String targetId) {

        ReactionType oldType = reaction.getReactionType();

        if (oldType == newType) {
            // Toggle off
            adjustPoints(targetType, oldType, null, targetId);
            reactionRepository.delete(reaction);
            return false;
        }

        // Update reaction
        adjustPoints(targetType, oldType, newType, targetId);
        reaction.setReactionType(newType);
        reactionRepository.save(reaction);
        return true;
    }

    /**
     * No existing reaction → create new
     */
    private boolean handleNewReaction(
            TargetType targetType, String targetId, ReactionType reactionType, Profile user) {

        Reaction newReaction = Reaction.builder()
                .targetType(targetType)
                .targetId(targetId)
                .user(user)
                .reactionType(reactionType)
                .build();

        try {
            reactionRepository.save(newReaction);
            adjustPoints(targetType, null, reactionType, targetId);
            return true;

        } catch (DataIntegrityViolationException e) {
            // Race condition → fetch & delegate to existing handler
            Reaction existing = reactionRepository
                    .findByTargetTypeAndTargetIdAndUser(targetType, targetId, user)
                    .orElseThrow(() -> new AppException(ErrorCode.DATA_INVALID));

            return handleExistingReaction(existing, reactionType, targetType, targetId);
        }
    }

    /**
     * Unified point update logic
     */
    private void adjustPoints(TargetType targetType,
                              ReactionType oldType, ReactionType newType,
                              String targetId) {

        // 1) Update blog reactionCount when reacting on BLOG
        if (targetType == TargetType.BLOG) {
            int delta = 0;

            // Remove effect of old reaction
            if (oldType == ReactionType.LIKE) {
                delta -= 1;
            } else if (oldType == ReactionType.DISLIKE) {
                delta += 1;
            }

            // Apply effect of new reaction
            if (newType == ReactionType.LIKE) {
                delta += 1;
            } else if (newType == ReactionType.DISLIKE) {
                delta -= 1;
            }

            if (delta != 0) {
                blogRepository.incrementReactionCount(targetId, delta);
                log.debug("ReactionServiceImpl_adjustPoints_Updated blog reactionCount by {} for blogId {}", delta, targetId);
            }
        }

        // 2) Update comment owner points when reacting on COMMENT
        if (targetType == TargetType.COMMENT) {
            // oldType = LIKE, newType = null   → remove like: -1
            // oldType = null, newType = LIKE   → add like: +1
            // oldType = LIKE, newType != LIKE  → unlike: -1
            // oldType != LIKE, newType = LIKE  → relike: +1

            int delta = 0;

            if (oldType == ReactionType.LIKE && newType != ReactionType.LIKE) {
                delta = -1;
            } else if (oldType != ReactionType.LIKE && newType == ReactionType.LIKE) {
                delta = 1;
            }

            if (delta != 0) {
                updateCommentOwnerPoints(targetId, delta);
            }
        }
    }


    /**
     * Toggle reaction and return both toggle result and count.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    @Transactional
    public ReactionToggleResult toggleReactionWithCount(TargetType targetType, String targetId, ReactionType reactionType) {
        boolean isAdded = toggleReaction(targetType, targetId, reactionType);
        // Blocking I/O - virtual thread yields here
        long count = getReactionCount(targetType, targetId, reactionType);

        // Increment activity count if reaction was added and target is COMMENT
        if (isAdded && targetType == TargetType.COMMENT && reactionType == ReactionType.LIKE) {
            try {
                Profile currentUser = getCurrentUser();
                activityService.incrementActivity(currentUser.getId());
            } catch (Exception e) {
                log.warn("ReactionServiceImpl_toggleReactionWithCount_Failed to increment activity for comment like: {}", e.getMessage());
                // Don't throw exception, activity tracking is not critical
            }
        }

        return ReactionToggleResult.builder()
                .isAdded(isAdded)
                .count(count)
                .build();
    }

    /**
     * Get reaction count for a target.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operation.
     * Virtual threads yield during database query, enabling high concurrency.
     */
    @Override
    public long getReactionCount(TargetType targetType, String targetId, ReactionType reactionType) {
        // Blocking I/O - virtual thread yields here
        return reactionRepository.countByTargetTypeAndTargetIdAndReactionType(targetType, targetId, reactionType);
    }

    /**
     * Check if current user has reacted to target.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    public boolean hasUserReacted(TargetType targetType, String targetId) {
        try {
            // Blocking I/O - virtual thread yields here
            String userId = AuthUtils.getCurrentUserId();
            Profile currentUser = profileRepository.findByUserId(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
            // Blocking I/O - virtual thread yields here
            return reactionRepository.existsByTargetTypeAndTargetIdAndUser(targetType, targetId, currentUser);
        } catch (Exception e) {
            log.debug("ReactionServiceImpl_hasUserReacted_Error checking user reaction: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Toggle reaction using string target type (converts to enum internally)
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     */
    @Override
    @Transactional
    @CacheEvict(value = {
            BlogCacheConstants.CACHE_BLOG,
            BlogCacheConstants.CACHE_BLOGS,
            BlogCacheConstants.CACHE_BLOGS_BY_AUTHOR,
            BlogCacheConstants.CACHE_POPULAR_BLOGS,
            BlogCacheConstants.CACHE_LIKED_BLOGS,
            BlogCacheConstants.CACHE_LATEST_BLOGS
    }, allEntries = true, condition = "#targetTypeString != null && #targetTypeString.toUpperCase() == 'BLOG'")
    public ReactionResponse toggleReaction(String targetTypeString, String targetId, ReactionType reactionType) {
        TargetType targetType = parseTargetType(targetTypeString);
        ReactionToggleResult result = toggleReactionWithCount(targetType, targetId, reactionType);
        
        return ReactionResponse.builder()
                .isReacted(result.isAdded())
                .count(result.getCount())
                .reactionType(reactionType)
                .build();
    }

    /**
     * Get reaction count using string target type (converts to enum internally)
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     */
    @Override
    public ReactionResponse getReactionCount(String targetTypeString, String targetId, ReactionType reactionType) {
        TargetType targetType = parseTargetType(targetTypeString);
        long count = getReactionCount(targetType, targetId, reactionType);
        boolean hasReacted = hasUserReacted(targetType, targetId);
        
        return ReactionResponse.builder()
                .isReacted(hasReacted)
                .count(count)
                .reactionType(reactionType)
                .build();
    }

    /**
     * Parse string to TargetType enum
     */
    private TargetType parseTargetType(String targetTypeString) {
        try {
            return TargetType.valueOf(targetTypeString.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("ReactionServiceImpl_parseTargetType_Invalid target type: {}", targetTypeString);
            throw new AppException(ErrorCode.COMMENT_TARGET_TYPE_INVALID);
        }
    }

    /**
     * Get reactions info for multiple targets (batch check).
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    public Map<String, ReactionInfo> getReactionsForTargets(TargetType targetType, List<String> targetIds, String userId) {
        if (targetIds == null || targetIds.isEmpty()) {
            return new HashMap<>();
        }

        // If user not logged in, return all false
        if (userId == null || userId.isBlank()) {
            return targetIds.stream()
                    .collect(Collectors.toMap(
                            id -> id,
                            id -> ReactionInfo.builder()
                                    .userReacted(false)
                                    .type(null)
                                    .build()
                    ));
        }

        try {
            // Blocking I/O - virtual thread yields here
            Profile user = profileRepository.findByUserId(userId)
                    .orElse(null);

            if (user == null) {
                return targetIds.stream()
                        .collect(Collectors.toMap(
                                id -> id,
                                id -> ReactionInfo.builder()
                                        .userReacted(false)
                                        .type(null)
                                        .build()
                        ));
            }

            // Blocking I/O - virtual thread yields here (batch query)
            List<Reaction> reactions = reactionRepository
                    .findByTargetTypeAndTargetIdInAndUser(targetType, targetIds, user);

            Map<String, Reaction> reactionMap = reactions.stream()
                    .collect(Collectors.toMap(Reaction::getTargetId, r -> r));

            Map<String, ReactionInfo> result = new HashMap<>();
            for (String targetId : targetIds) {
                Reaction reaction = reactionMap.get(targetId);
                if (reaction != null) {
                    result.put(targetId, ReactionInfo.builder()
                            .userReacted(true)
                            .type(reaction.getReactionType())
                            .build());
                } else {
                    result.put(targetId, ReactionInfo.builder()
                            .userReacted(false)
                            .type(null)
                            .build());
                }
            }
            return result;
        } catch (Exception e) {
            log.debug("ReactionServiceImpl_getReactionsForTargets_Error checking user reactions: {}", e.getMessage());
            // Return all false on error
            return targetIds.stream()
                    .collect(Collectors.toMap(
                            id -> id,
                            id -> ReactionInfo.builder()
                                    .userReacted(false)
                                    .type(null)
                                    .build()
                    ));
        }
    }

    /**
     * Cập nhật points cho comment owner.
     * Let exception propagate to ensure transaction rollback on failure.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during each database operation, enabling high concurrency.
     */
    private void updateCommentOwnerPoints(String commentId, int pointsChange) {
        // Blocking I/O - virtual thread yields here
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();
            String commentOwnerUserId = comment.getAuthor().getUser().getId();

            // Blocking I/O - virtual thread yields here
            if (pointsChange > 0) {
                userStatsRepository.updateUserStats(commentOwnerUserId, pointsChange, 0, 0, 0, 0, 0);
                log.debug("ReactionServiceImpl_updateCommentOwnerPoints_Increased {} points for comment owner: {}", pointsChange, commentOwnerUserId);
            } else {
                userStatsRepository.updateUserStats(commentOwnerUserId, pointsChange, 0, 0, 0, 0, 0);
                log.debug("ReactionServiceImpl_updateCommentOwnerPoints_Decreased {} points for comment owner: {}", Math.abs(pointsChange), commentOwnerUserId);
            }
        }
    }

    /**
     * Get all reactions with pagination.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    public PagingResponse<ReactionDetailResponse> getAllReactions(PagingRequest request) {
        log.debug("ReactionServiceImpl_getAllReactions_Fetching reactions with page: {}, size: {}", request.getPage(), request.getPageSize());

        Pageable pageable = PagingUtil.createPageable(request);

        // Blocking I/O - virtual thread yields here
        Page<Reaction> reactions = reactionRepository.findAllWithUser(pageable);

        return PagingResponse.<ReactionDetailResponse>builder()
                .currentPage(reactions.getNumber())
                .totalPages(reactions.getTotalPages())
                .pageSize(reactions.getSize())
                .totalElement(reactions.getTotalElements())
                .data(reactions.getContent().stream()
                        .map(reactionMapper::toReactionDetailResponse)
                        .toList())
                .build();
    }

    /**
     * Get all reactions by target type with pagination.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    public PagingResponse<ReactionDetailResponse> getAllReactionsByTargetType(String targetTypeString, PagingRequest request) {
        log.debug("ReactionServiceImpl_getAllReactionsByTargetType_Fetching reactions for target type: {} with page: {}, size: {}", 
                targetTypeString, request.getPage(), request.getPageSize());

        TargetType targetType = parseTargetType(targetTypeString);
        Pageable pageable = PagingUtil.createPageable(request);

        // Blocking I/O - virtual thread yields here
        Page<Reaction> reactions = reactionRepository.findAllByTargetTypeWithUser(targetType, pageable);

        return PagingResponse.<ReactionDetailResponse>builder()
                .currentPage(reactions.getNumber())
                .totalPages(reactions.getTotalPages())
                .pageSize(reactions.getSize())
                .totalElement(reactions.getTotalElements())
                .data(reactions.getContent().stream()
                        .map(reactionMapper::toReactionDetailResponse)
                        .toList())
                .build();
    }
}

