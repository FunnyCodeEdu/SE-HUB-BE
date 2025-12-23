package com.se.hub.modules.gamification.repository;

import com.se.hub.modules.gamification.entity.Streak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StreakRepository extends JpaRepository<Streak, String> {
    Optional<Streak> findByGamificationProfileId(String profileId);
}


