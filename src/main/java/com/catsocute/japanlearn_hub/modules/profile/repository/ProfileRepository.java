package com.catsocute.japanlearn_hub.modules.profile.repository;

import com.catsocute.japanlearn_hub.modules.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, String> {
    boolean existsByUserId(String userId);
    Optional<Profile> findByUserId(String userId);
}
