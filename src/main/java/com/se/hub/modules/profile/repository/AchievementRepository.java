package com.se.hub.modules.profile.repository;

import com.se.hub.modules.profile.entity.Achievement;
import com.se.hub.modules.profile.enums.AchievementEnums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement,String> {
    boolean existsByAchievementType(AchievementEnums achievementType);
    void deleteByAchievementType(AchievementEnums achievementType);
    Optional<Achievement> findByAchievementType(AchievementEnums achievementType);
}
