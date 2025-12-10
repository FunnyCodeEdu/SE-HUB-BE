package com.se.hub.modules.gamification.repository;

import com.se.hub.modules.gamification.entity.StreakReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RewardStreakRepository extends JpaRepository<StreakReward, String> {
}


