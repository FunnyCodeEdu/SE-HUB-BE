package com.se.hub.modules.gamification.service;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.gamification.dto.request.CreateStreakRewardRequest;
import com.se.hub.modules.gamification.dto.request.UpdateStreakRewardRequest;
import com.se.hub.modules.gamification.dto.response.StreakRewardResponse;

public interface StreakRewardService {
    /**
     * Create a new streak reward with optional rewards
     * @param request Streak reward creation request
     * @return StreakRewardResponse with created streak reward data
     */
    StreakRewardResponse createStreakReward(CreateStreakRewardRequest request);

    /**
     * Get streak reward by ID
     * @param streakRewardId Streak reward ID
     * @return StreakRewardResponse with streak reward data
     */
    StreakRewardResponse getStreakRewardById(String streakRewardId);

    /**
     * Get all streak rewards with pagination
     * @param request Paging request
     * @return PagingResponse with list of streak rewards
     */
    PagingResponse<StreakRewardResponse> getAllStreakRewards(PagingRequest request);

    /**
     * Update streak reward by ID (partial update supported)
     * @param streakRewardId Streak reward ID
     * @param request Update request
     * @return StreakRewardResponse with updated streak reward data
     */
    StreakRewardResponse updateStreakReward(String streakRewardId, UpdateStreakRewardRequest request);

    /**
     * Delete streak reward by ID
     * @param streakRewardId Streak reward ID
     */
    void deleteStreakReward(String streakRewardId);
}


