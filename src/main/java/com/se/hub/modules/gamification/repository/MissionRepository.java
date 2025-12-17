package com.se.hub.modules.gamification.repository;

import com.se.hub.modules.gamification.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, String> {
    @Query(value = "SELECT * FROM mission WHERE type = :type AND active = true ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Mission> findRandomByTypeAndActiveTrue(@Param("type") String type, @Param("limit") int limit);
}


