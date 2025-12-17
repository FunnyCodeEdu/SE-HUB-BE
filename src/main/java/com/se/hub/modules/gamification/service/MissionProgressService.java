package com.se.hub.modules.gamification.service;

import com.se.hub.modules.gamification.dto.response.MissionProgressResponse;

import java.util.List;

public interface MissionProgressService {
    /**
     * Get daily mission progress for current user
     * If no progress exists or date is expired, create new progress
     * @return List of 5 MissionProgressResponse
     * @author catsocute
     */
    List<MissionProgressResponse> getDailyMissionProgress();
}

