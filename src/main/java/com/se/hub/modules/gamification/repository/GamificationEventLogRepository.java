package com.se.hub.modules.gamification.repository;

import com.se.hub.modules.gamification.entity.GamificationEventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamificationEventLogRepository extends JpaRepository<GamificationEventLog, String> {
}


