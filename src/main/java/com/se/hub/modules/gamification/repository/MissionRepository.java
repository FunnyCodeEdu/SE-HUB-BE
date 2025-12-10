package com.se.hub.modules.gamification.repository;

import com.se.hub.modules.gamification.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionRepository extends JpaRepository<Mission, String> {
}


