package com.se.hub.modules.gamification.repository;

import com.se.hub.modules.gamification.entity.ClaimedStreakReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimedStreakRewardRepository extends JpaRepository<ClaimedStreakReward, String> {
    boolean existsByGamificationProfileIdAndStreakRewardId(String gamificationProfileId, String streakRewardId);
}


