package com.se.hub.modules.gamification.repository;

import com.se.hub.modules.gamification.entity.StreakReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StreakRewardRepository extends JpaRepository<StreakReward, String> {
    List<StreakReward> findByActiveTrueAndStreakTargetLessThanEqual(int streakTarget);
}


