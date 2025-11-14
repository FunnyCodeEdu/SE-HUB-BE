package com.se.hub.modules.notification.service.api;

import com.se.hub.modules.notification.enums.NotificationTemplateType;

/**
 * Service for managing notification templates
 * Handles template loading, rendering, and i18n support
 */
public interface NotificationTemplateService {
    /**
     * Get template title for a notification type
     * @param templateType template type
     * @param args arguments for template formatting
     * @return formatted title
     */
    String getTemplateTitle(NotificationTemplateType templateType, Object... args);

    /**
     * Get template content for a notification type
     * @param templateType template type
     * @param args arguments for template formatting
     * @return formatted content
     */
    String getTemplateContent(NotificationTemplateType templateType, Object... args);
}


