package com.catsocute.japanlearn_hub.modules.profile.repository;

import com.catsocute.japanlearn_hub.modules.profile.entity.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, String> {
    Optional<UserStats> findByProfileId(String id);
    boolean existsByProfileId(String id);

    @Modifying
    @Query("""
        UPDATE UserStats us
        SET us.points = us.points + :points,
            us.examsDone = us.examsDone + :examsDone,
            us.cmtCount = us.cmtCount + :cmtCount,
            us.docsUploaded = us.docsUploaded + :docsUploaded,
            us.blogsUploaded = us.blogsUploaded + :blogsUploaded,
            us.blogsShared = us.blogsShared + :blogsShared,
            us.updatedDate = CURRENT_TIMESTAMP
        WHERE us.profile.user.id = :userId
        """)
    void updateUserStats(@Param("userId") String userId,
                         @Param("points") int points,
                         @Param("examsDone") int examsDone,
                         @Param("cmtCount") int cmtCount,
                         @Param("docsUploaded") int docsUploaded,
                         @Param("blogsUploaded") int blogsUploaded,
                         @Param("blogsShared") int blogsShared);
}
