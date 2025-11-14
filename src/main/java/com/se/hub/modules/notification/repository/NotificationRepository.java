package com.se.hub.modules.notification.repository;

import com.se.hub.modules.notification.entity.Notification;
import com.se.hub.modules.notification.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String>, JpaSpecificationExecutor<Notification> {
    List<Notification> findAllByNotificationType(NotificationType notificationType);
}



