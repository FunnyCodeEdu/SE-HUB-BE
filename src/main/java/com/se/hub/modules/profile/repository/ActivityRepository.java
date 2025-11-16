package com.se.hub.modules.profile.repository;

import com.se.hub.modules.profile.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {
    
    Optional<Activity> findByProfileIdAndActivityDate(String profileId, LocalDate activityDate);
    
    List<Activity> findByProfileIdAndActivityDateBetween(String profileId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Increment activity count for a profile on a specific date.
     * If record doesn't exist, creates new one with count = 1.
     * Uses PostgreSQL ON CONFLICT for upsert operation.
     */
    @Modifying
    @Query(value = """
        INSERT INTO activity (id, profile_id, activity_date, count, created_by, create_date, updated_by, update_date)
        VALUES (gen_random_uuid()::text, :profileId, :activityDate, 1, :userId, CURRENT_TIMESTAMP, :userId, CURRENT_TIMESTAMP)
        ON CONFLICT (profile_id, activity_date)
        DO UPDATE SET 
            count = activity.count + 1,
            updated_by = :userId,
            update_date = CURRENT_TIMESTAMP
        """, nativeQuery = true)
    void incrementActivityCount(@Param("profileId") String profileId,
                                @Param("activityDate") LocalDate activityDate,
                                @Param("userId") String userId);
}

