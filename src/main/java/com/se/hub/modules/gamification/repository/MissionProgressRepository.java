package com.se.hub.modules.gamification.repository;

import com.se.hub.modules.gamification.entity.MissionProgress;
import com.se.hub.modules.gamification.enums.MissionTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MissionProgressRepository extends JpaRepository<MissionProgress, String> {
    List<MissionProgress> findByGamificationProfileId(String profileId);
    
    void deleteByGamificationProfileId(String profileId);
    
    List<MissionProgress> findByGamificationProfileIdAndMissionTargetType(String profileId, MissionTargetType targetType);

    List<MissionProgress> findByGamificationProfileIdAndStartAt(String profileId, LocalDate startAt);
}


