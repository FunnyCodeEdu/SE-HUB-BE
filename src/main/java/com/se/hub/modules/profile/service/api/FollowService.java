package com.se.hub.modules.profile.service.api;

import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.profile.dto.response.FollowCountResponse;
import com.se.hub.modules.profile.dto.response.ProfileResponse;

import java.util.List;

public interface FollowService {
    
    /**
     * Follow a user
     * @param followingUserId User ID to follow
     */
    void followUser(String followingUserId);
    
    /**
     * Unfollow a user
     * @param followingUserId User ID to unfollow
     */
    void unfollowUser(String followingUserId);
    
    /**
     * Check if current user is following a user
     * @param userId User ID to check
     * @return true if following, false otherwise
     */
    boolean isFollowing(String userId);
    
    /**
     * Get list of users that current user is following
     * @param page Page number
     * @param size Page size
     * @return PagingResponse of ProfileResponse
     */
    PagingResponse<ProfileResponse> getFollowing(int page, int size);
    
    /**
     * Get list of users that follow current user (followers)
     * @param page Page number
     * @param size Page size
     * @return PagingResponse of ProfileResponse
     */
    PagingResponse<ProfileResponse> getFollowers(int page, int size);
    
    /**
     * Get list of users that a specific user is following
     * @param userId User ID
     * @param page Page number
     * @param size Page size
     * @return PagingResponse of ProfileResponse
     */
    PagingResponse<ProfileResponse> getFollowingByUserId(String userId, int page, int size);
    
    /**
     * Get list of users that follow a specific user (followers)
     * @param userId User ID
     * @param page Page number
     * @param size Page size
     * @return PagingResponse of ProfileResponse
     */
    PagingResponse<ProfileResponse> getFollowersByUserId(String userId, int page, int size);
    
    /**
     * Get follow count (followers and following) for current user
     * @return FollowCountResponse with followersCount and followingCount
     */
    FollowCountResponse getFollowCount();
    
    /**
     * Get follow count (followers and following) for a specific user
     * @param userId User ID
     * @return FollowCountResponse with followersCount and followingCount
     */
    FollowCountResponse getFollowCountByUserId(String userId);
    
    /**
     * Get mutual friends (users that both current user and they follow each other)
     * @return List of ProfileResponse for mutual friends
     */
    List<ProfileResponse> getMutualFriends();
}

