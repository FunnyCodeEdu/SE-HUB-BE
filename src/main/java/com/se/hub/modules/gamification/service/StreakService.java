package com.se.hub.modules.gamification.service;

public interface StreakService {

    /**
     * Increment streak for profile and handle log + reward if eligible.
     * @param gamificationProfileId profile id
     * @author catsocute
     */
    void incrementStreakAndHandleReward(String gamificationProfileId);
}

