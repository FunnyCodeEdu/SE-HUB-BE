package com.se.hub.modules.profile.repository;

import com.se.hub.modules.profile.entity.UserLevel;
import com.se.hub.modules.profile.enums.LevelEnums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLevelRepository extends JpaRepository<UserLevel,String> {
    boolean existsByLevel(LevelEnums level);
    Optional<UserLevel> findByLevel(LevelEnums level);

    /**
     * find user level by points: min < points < max
     *
     * @author catsocute
     * @param points int
     * @return UserLevel
     */
    @Query("""
            SELECT ul
            FROM UserLevel ul
            WHERE :points >= ul.minPoints AND :points < ul.maxPoints
            """)
    Optional<UserLevel> findLevelByPoints(int points);
}
