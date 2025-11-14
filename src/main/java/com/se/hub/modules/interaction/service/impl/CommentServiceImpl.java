package com.se.hub.modules.interaction.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.interaction.dto.request.CreateCommentRequest;
import com.se.hub.modules.interaction.dto.request.UpdateCommentRequest;
import com.se.hub.modules.interaction.dto.response.CommentResponse;
import com.se.hub.modules.interaction.entity.Comment;
import com.se.hub.modules.interaction.enums.TargetType;
import com.se.hub.modules.interaction.exception.InteractionErrorCode;
import com.se.hub.modules.interaction.mapper.CommentMapper;
import com.se.hub.modules.interaction.repository.CommentRepository;
import com.se.hub.modules.interaction.service.api.CommentService;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.profile.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Comment Service Implementation
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
public class CommentServiceImpl implements CommentService {
    
    CommentRepository commentRepository;
    CommentMapper commentMapper;
    ProfileRepository profileRepository;

    /**
     * Create a new comment.
     * Virtual Thread Best Practice: Uses @Transactional with synchronous blocking I/O operations.
     * Virtual threads yield during each database operation, enabling high concurrency.
     */
    @Override
    @Transactional
    public CommentResponse createComment(CreateCommentRequest request) {
        log.debug("CommentServiceImpl_createComment_Creating new comment for user: {}", AuthUtils.getCurrentUserId());
        
        Comment comment = commentMapper.toComment(request);
        
        // Blocking I/O - virtual thread yields here
        String userId = AuthUtils.getCurrentUserId();
        Profile author = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("CommentServiceImpl_createComment_Profile for user id {} not found", userId);
                    return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                });
        comment.setAuthor(author);
        
        // Set parent comment if provided
        if (request.getParentCommentId() != null && !request.getParentCommentId().isEmpty()) {
            // Blocking I/O - virtual thread yields here
            Comment parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> {
                        log.error("CommentServiceImpl_createComment_Parent comment id {} not found", request.getParentCommentId());
                        return InteractionErrorCode.COMMENT_NOT_FOUND.toException();
                    });
            comment.setParentComment(parentComment);
        }
        
        comment.setCreatedBy(userId);
        comment.setUpdateBy(userId);

        // Blocking I/O - virtual thread yields here
        CommentResponse response = commentMapper.toCommentResponse(commentRepository.save(comment));
        log.debug("CommentServiceImpl_createComment_Comment created successfully with id: {}", response.getId());
        return response;
    }

    /**
     * Get comment by ID.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operation.
     * Virtual threads yield during database query, enabling high concurrency.
     */
    @Override
    public CommentResponse getById(String commentId) {
        log.debug("CommentServiceImpl_getById_Fetching comment with id: {}", commentId);
        // Blocking I/O - virtual thread yields here
        return commentMapper.toCommentResponse(commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("CommentServiceImpl_getById_Comment id {} not found", commentId);
                    return InteractionErrorCode.COMMENT_NOT_FOUND.toException();
                }));
    }

    /**
     * Get comments by target type and target ID.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    public PagingResponse<CommentResponse> getCommentsByTarget(String targetType, String targetId, PagingRequest request) {
        log.debug("CommentServiceImpl_getCommentsByTarget_Fetching comments for target: {} {}", targetType, targetId);
        
        TargetType type;
        try {
            type = TargetType.valueOf(targetType.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("CommentServiceImpl_getCommentsByTarget_Invalid target type: {}", targetType);
            throw InteractionErrorCode.COMMENT_TARGET_TYPE_INVALID.toException();
        }

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        // Blocking I/O - virtual thread yields here
        Page<Comment> comments = commentRepository.findByTargetTypeAndTargetId(type, targetId, pageable);

        return PagingResponse.<CommentResponse>builder()
                .currentPage(comments.getNumber())
                .totalPages(comments.getTotalPages())
                .pageSize(comments.getSize())
                .totalElement(comments.getTotalElements())
                .data(comments.getContent().stream()
                        .map(commentMapper::toCommentResponse)
                        .toList()
                )
                .build();
    }

    /**
     * Get parent comments by target (excluding replies).
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    public PagingResponse<CommentResponse> getParentCommentsByTarget(String targetType, String targetId, PagingRequest request) {
        log.debug("CommentServiceImpl_getParentCommentsByTarget_Fetching parent comments for target: {} {}", targetType, targetId);
        
        TargetType type;
        try {
            type = TargetType.valueOf(targetType.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("CommentServiceImpl_getParentCommentsByTarget_Invalid target type: {}", targetType);
            throw InteractionErrorCode.COMMENT_TARGET_TYPE_INVALID.toException();
        }

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        // Blocking I/O - virtual thread yields here
        Page<Comment> comments = commentRepository.findByTargetTypeAndTargetIdAndParentCommentIsNull(type, targetId, pageable);

        return PagingResponse.<CommentResponse>builder()
                .currentPage(comments.getNumber())
                .totalPages(comments.getTotalPages())
                .pageSize(comments.getSize())
                .totalElement(comments.getTotalElements())
                .data(comments.getContent().stream()
                        .map(commentMapper::toCommentResponse)
                        .toList()
                )
                .build();
    }

    /**
     * Get replies for a parent comment.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    public PagingResponse<CommentResponse> getRepliesByParentComment(String parentCommentId, PagingRequest request) {
        log.debug("CommentServiceImpl_getRepliesByParentComment_Fetching replies for parent comment: {}", parentCommentId);
        
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        // Blocking I/O - virtual thread yields here
        Page<Comment> comments = commentRepository.findByParentCommentId(parentCommentId, pageable);

        return PagingResponse.<CommentResponse>builder()
                .currentPage(comments.getNumber())
                .totalPages(comments.getTotalPages())
                .pageSize(comments.getSize())
                .totalElement(comments.getTotalElements())
                .data(comments.getContent().stream()
                        .map(commentMapper::toCommentResponse)
                        .toList()
                )
                .build();
    }

    /**
     * Get comments by author ID.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    public PagingResponse<CommentResponse> getCommentsByAuthor(String authorId, PagingRequest request) {
        log.debug("CommentServiceImpl_getCommentsByAuthor_Fetching comments for author: {}", authorId);
        
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        // Blocking I/O - virtual thread yields here
        Page<Comment> comments = commentRepository.findByAuthorId(authorId, pageable);

        return PagingResponse.<CommentResponse>builder()
                .currentPage(comments.getNumber())
                .totalPages(comments.getTotalPages())
                .pageSize(comments.getSize())
                .totalElement(comments.getTotalElements())
                .data(comments.getContent().stream()
                        .map(commentMapper::toCommentResponse)
                        .toList()
                )
                .build();
    }

    /**
     * Get all comments with pagination.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    public PagingResponse<CommentResponse> getComments(PagingRequest request) {
        log.debug("CommentServiceImpl_getComments_Fetching comments with page: {}, size: {}", request.getPage(), request.getPageSize());
        
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        // Blocking I/O - virtual thread yields here
        Page<Comment> comments = commentRepository.findAll(pageable);

        return PagingResponse.<CommentResponse>builder()
                .currentPage(comments.getNumber())
                .totalPages(comments.getTotalPages())
                .pageSize(comments.getSize())
                .totalElement(comments.getTotalElements())
                .data(comments.getContent().stream()
                        .map(commentMapper::toCommentResponse)
                        .toList()
                )
                .build();
    }

    /**
     * Update comment by ID.
     * Virtual Thread Best Practice: Uses @Transactional with synchronous blocking I/O operations.
     * Virtual threads yield during each database operation, enabling high concurrency.
     */
    @Override
    @Transactional
    public CommentResponse updateCommentById(String commentId, UpdateCommentRequest request) {
        log.debug("CommentServiceImpl_updateCommentById_Updating comment with id: {}", commentId);
        
        // Blocking I/O - virtual thread yields here
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("CommentServiceImpl_updateCommentById_Comment id {} not found", commentId);
                    return InteractionErrorCode.COMMENT_NOT_FOUND.toException();
                });

        comment = commentMapper.updateCommentFromRequest(comment, request);

        // Blocking I/O - virtual thread yields here
        CommentResponse response = commentMapper.toCommentResponse(commentRepository.save(comment));
        log.debug("CommentServiceImpl_updateCommentById_Comment updated successfully with id: {}", commentId);
        return response;
    }

    /**
     * Delete comment by ID.
     * Virtual Thread Best Practice: Uses @Transactional with synchronous blocking I/O operations.
     * Virtual threads yield during database operations, enabling high concurrency.
     */
    @Override
    @Transactional
    public void deleteCommentById(String commentId) {
        log.debug("CommentServiceImpl_deleteCommentById_Deleting comment with id: {}", commentId);
        
        // Blocking I/O - virtual thread yields here
        if (!commentRepository.existsById(commentId)) {
            log.error("CommentServiceImpl_deleteCommentById_Comment id {} not found", commentId);
            throw InteractionErrorCode.COMMENT_NOT_FOUND.toException();
        }
        
        commentRepository.deleteById(commentId);
        log.debug("CommentServiceImpl_deleteCommentById_Comment deleted successfully with id: {}", commentId);
    }
}

