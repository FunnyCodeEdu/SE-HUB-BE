package com.se.hub.modules.notification.repository;

import com.se.hub.modules.notification.entity.NotificationTemplate;
import com.se.hub.modules.notification.enums.NotificationTemplateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, String> {
    Optional<NotificationTemplate> findByTemplateType(NotificationTemplateType templateType);
}



