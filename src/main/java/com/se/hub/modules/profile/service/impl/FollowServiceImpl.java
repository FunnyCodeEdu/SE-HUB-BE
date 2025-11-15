package com.se.hub.modules.profile.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.profile.dto.response.FollowCountResponse;
import com.se.hub.modules.profile.dto.response.ProfileResponse;
import com.se.hub.modules.profile.entity.Follow;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.profile.mapper.ProfileMapper;
import com.se.hub.modules.profile.repository.FollowRepository;
import com.se.hub.modules.profile.repository.ProfileRepository;
import com.se.hub.modules.profile.service.api.FollowService;
import com.se.hub.modules.user.entity.User;
import com.se.hub.modules.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FollowServiceImpl implements FollowService {
    
    FollowRepository followRepository;
    UserRepository userRepository;
    ProfileRepository profileRepository;
    ProfileMapper profileMapper;
    
    @Override
    @Transactional
    public void followUser(String followingUserId) {
        String currentUserId = AuthUtils.getCurrentUserId();
        
        // Cannot follow yourself
        if (currentUserId.equals(followingUserId)) {
            log.warn("User {} attempted to follow themselves", currentUserId);
            throw new AppException(ErrorCode.DATA_INVALID);
        }
        
        // Get current user (follower)
        User follower = userRepository.findById(currentUserId)
                .orElseThrow(() -> {
                    log.error("Follower user not found: {}", currentUserId);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });
        
        // Get user to follow (following)
        User following = userRepository.findById(followingUserId)
                .orElseThrow(() -> {
                    log.error("Following user not found: {}", followingUserId);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });
        
        // Check if already following
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            log.warn("User {} is already following user {}", currentUserId, followingUserId);
            throw new AppException(ErrorCode.DATA_EXISTED);
        }
        
        // Create follow relationship
        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();
        
        followRepository.save(follow);
        log.info("User {} followed user {}", currentUserId, followingUserId);
    }
    
    @Override
    @Transactional
    public void unfollowUser(String followingUserId) {
        String currentUserId = AuthUtils.getCurrentUserId();
        
        // Get current user (follower)
        User follower = userRepository.findById(currentUserId)
                .orElseThrow(() -> {
                    log.error("Follower user not found: {}", currentUserId);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });
        
        // Get user to unfollow (following)
        User following = userRepository.findById(followingUserId)
                .orElseThrow(() -> {
                    log.error("Following user not found: {}", followingUserId);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });
        
        // Find and delete follow relationship
        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> {
                    log.warn("User {} is not following user {}", currentUserId, followingUserId);
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                });
        
        followRepository.delete(follow);
        log.info("User {} unfollowed user {}", currentUserId, followingUserId);
    }
    
    @Override
    public boolean isFollowing(String userId) {
        String currentUserId = AuthUtils.getCurrentUserId();
        
        User follower = userRepository.findById(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        User following = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }
    
    @Override
    public PagingResponse<ProfileResponse> getFollowing(int page, int size) {
        String currentUserId = AuthUtils.getCurrentUserId();
        return getFollowingByUserId(currentUserId, page, size);
    }
    
    @Override
    public PagingResponse<ProfileResponse> getFollowers(int page, int size) {
        String currentUserId = AuthUtils.getCurrentUserId();
        return getFollowersByUserId(currentUserId, page, size);
    }
    
    @Override
    public PagingResponse<ProfileResponse> getFollowingByUserId(String userId, int page, int size) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        
        Pageable pageable = PageRequest.of(page - GlobalVariable.PAGE_SIZE_INDEX, size);
        Page<User> followingPage = followRepository.findFollowingByUserId(userId, pageable);
        
        // Convert User to ProfileResponse
        List<ProfileResponse> profiles = followingPage.getContent().stream()
                .map(u -> {
                    Profile profile = profileRepository.findByUserId(u.getId())
                            .orElse(null);
                    if (profile != null) {
                        return profileMapper.toProfileResponse(profile);
                    }
                    return null;
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
        
        return PagingResponse.<ProfileResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(followingPage.getTotalPages())
                .totalElement(followingPage.getTotalElements())
                .data(profiles)
                .build();
    }
    
    @Override
    public PagingResponse<ProfileResponse> getFollowersByUserId(String userId, int page, int size) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        
        Pageable pageable = PageRequest.of(page - GlobalVariable.PAGE_SIZE_INDEX, size);
        Page<User> followersPage = followRepository.findFollowersByUserId(userId, pageable);
        
        // Convert User to ProfileResponse
        List<ProfileResponse> profiles = followersPage.getContent().stream()
                .map(u -> {
                    Profile profile = profileRepository.findByUserId(u.getId())
                            .orElse(null);
                    if (profile != null) {
                        return profileMapper.toProfileResponse(profile);
                    }
                    return null;
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
        
        return PagingResponse.<ProfileResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(followersPage.getTotalPages())
                .totalElement(followersPage.getTotalElements())
                .data(profiles)
                .build();
    }
    
    @Override
    public FollowCountResponse getFollowCount() {
        String currentUserId = AuthUtils.getCurrentUserId();
        return getFollowCountByUserId(currentUserId);
    }
    
    @Override
    public FollowCountResponse getFollowCountByUserId(String userId) {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        // Count followers (số người follow user này)
        long followersCount = followRepository.countByFollowing(user);
        
        // Count following (số người mà user này đang follow)
        long followingCount = followRepository.countByFollower(user);
        
        return FollowCountResponse.builder()
                .followersCount(followersCount)
                .followingCount(followingCount)
                .build();
    }
}

