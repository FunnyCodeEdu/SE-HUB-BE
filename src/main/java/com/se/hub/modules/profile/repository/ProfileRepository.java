package com.se.hub.modules.profile.repository;

import com.se.hub.modules.profile.entity.Profile;
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
}
