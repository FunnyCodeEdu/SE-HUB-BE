package com.se.hub.modules.gamification.repository;

import com.se.hub.modules.gamification.entity.GamificationProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamificationProfileRepository extends JpaRepository<GamificationProfile, String> {
    boolean existsByProfile_Id(String profileId);
}

