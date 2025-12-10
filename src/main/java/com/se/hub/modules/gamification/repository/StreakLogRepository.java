package com.se.hub.modules.gamification.repository;

import com.se.hub.modules.gamification.entity.StreakLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StreakLogRepository extends JpaRepository<StreakLog, String> {
}


