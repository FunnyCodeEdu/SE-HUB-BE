package com.se.hub.modules.profile.repository;

import com.se.hub.modules.profile.entity.PrivacySetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivacySettingRepository extends JpaRepository<PrivacySetting, String> {
    Optional<PrivacySetting> findByUser_User_Id(String userId);
}

