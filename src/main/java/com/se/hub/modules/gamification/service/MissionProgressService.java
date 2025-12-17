package com.se.hub.modules.gamification.service;

import com.se.hub.modules.gamification.dto.response.MissionProgressResponse;
import com.se.hub.modules.gamification.enums.MissionTargetType;

import java.util.List;

public interface MissionProgressService {
    /**
     * Get daily mission progress for current user
     * If no progress exists or date is expired, create new progress
     * @return List of 5 MissionProgressResponse
     * @author catsocute
     */
    List<MissionProgressResponse> getDailyMissionProgress();

    /**
     * Update current value for mission progress by profile and target type
     * @param profileId gamification profile id
     * @param targetType mission target type (LIKE, COMMENT, BLOG, EXAM)
     * @author catsocute
     */
    void updateCurrentValue(String profileId, MissionTargetType targetType);
}

