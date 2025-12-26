package com.se.hub.modules.gamification.service;

import com.se.hub.modules.gamification.dto.request.CreateRewardRequest;
import com.se.hub.modules.gamification.entity.GamificationEventLog;
import com.se.hub.modules.gamification.entity.GamificationProfile;
import com.se.hub.modules.gamification.entity.Reward;
import com.se.hub.modules.gamification.enums.ActionType;

import java.util.List;

public interface RewardService {
    /**
     * Create multiple rewards from CreateRewardRequest list
     * @param requests List of CreateRewardRequest
     * @param userId User ID for createdBy and updateBy fields
     * @return List of saved Reward entities
     */
    List<Reward> createRewards(List<CreateRewardRequest> requests, String userId);

    void handleReward(Reward reward, GamificationProfile profile, ActionType type);

}

