package com.se.hub.modules.notification.repository;

import com.se.hub.modules.notification.entity.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, String> {
    Optional<NotificationSetting> findByUser_Id(String userId);
}



