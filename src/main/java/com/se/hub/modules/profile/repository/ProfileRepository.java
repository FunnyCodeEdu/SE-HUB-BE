package com.se.hub.modules.profile.repository;

import com.se.hub.modules.profile.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, String> {
    boolean existsByUserId(String userId);
    Optional<Profile> findByUserId(String userId);
    
    /**
     * Find all profiles by list of user IDs
     * @param userIds list of user IDs
     * @return list of profiles
     */
    @Query("SELECT p FROM Profile p WHERE p.user.id IN :userIds")
    List<Profile> findAllByUserIds(@Param("userIds") List<String> userIds);

    @Query(value = """
            SELECT DISTINCT p FROM Profile p
            LEFT JOIN FETCH p.user u
            LEFT JOIN FETCH u.role r
            WHERE LOWER(COALESCE(p.fullName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(p.username, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(p.email, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """,
            countQuery = """
            SELECT COUNT(DISTINCT p.id) FROM Profile p
            LEFT JOIN p.user u
            WHERE LOWER(COALESCE(p.fullName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(p.username, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(p.email, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Profile> searchProfiles(@Param("keyword") String keyword, Pageable pageable);
}
