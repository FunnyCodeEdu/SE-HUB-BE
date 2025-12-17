package com.se.hub.modules.gamification.repository;

import com.se.hub.modules.gamification.entity.MissionProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionProgressRepository extends JpaRepository<MissionProgress, String> {
    List<MissionProgress> findByGamificationProfileId(String profileId);
    
    void deleteByGamificationProfileId(String profileId);
}


