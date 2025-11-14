package com.se.hub.modules.notification.service.impl;

import com.se.hub.modules.notification.constant.NotificationTemplateConstants;
import com.se.hub.modules.notification.enums.NotificationTemplateType;
import com.se.hub.modules.notification.repository.NotificationTemplateRepository;
import com.se.hub.modules.notification.service.api.NotificationTemplateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * Template service implementation
 * Loads templates from database or constants and renders with placeholders
 * Supports i18n (can be extended)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationTemplateServiceImpl implements NotificationTemplateService {
    NotificationTemplateRepository notificationTemplateRepository;

    @Override
    public String getTemplateTitle(NotificationTemplateType templateType, Object... args) {
        // Try to get from database first
        var template = notificationTemplateRepository.findByTemplateType(templateType);
        if (template.isPresent()) {
            return formatTemplate(template.get().getTemplateTitle(), args);
        }

        // Fallback to constants
        return formatTemplateFromConstants(templateType, true, args);
    }

    @Override
    public String getTemplateContent(NotificationTemplateType templateType, Object... args) {
        // Try to get from database first
        var template = notificationTemplateRepository.findByTemplateType(templateType);
        if (template.isPresent()) {
            return formatTemplate(template.get().getTemplateContent(), args);
        }

        // Fallback to constants
        return formatTemplateFromConstants(templateType, false, args);
    }

    /**
     * Format template string with arguments
     */
    private String formatTemplate(String template, Object... args) {
        if (args == null || args.length == 0) {
            return template;
        }
        try {
            return MessageFormat.format(template, args);
        } catch (Exception e) {
            log.warn("NotificationTemplateService_formatTemplate_Error formatting template: {}", template, e);
            return template;
        }
    }

    /**
     * Get template from constants and format
     */
    private String formatTemplateFromConstants(NotificationTemplateType templateType, boolean isTitle, Object... args) {
        String template = switch (templateType) {
            case MENTION -> isTitle 
                ? NotificationTemplateConstants.TEMPLATE_TITLE_MENTION
                : NotificationTemplateConstants.TEMPLATE_CONTENT_MENTION;
            case POST_LIKED -> isTitle
                ? NotificationTemplateConstants.TEMPLATE_TITLE_POST_LIKED
                : NotificationTemplateConstants.TEMPLATE_CONTENT_POST_LIKED;
            case POST_COMMENTED -> isTitle
                ? NotificationTemplateConstants.TEMPLATE_TITLE_POST_COMMENTED
                : NotificationTemplateConstants.TEMPLATE_CONTENT_POST_COMMENTED;
            case BLOG_APPROVED -> isTitle
                ? NotificationTemplateConstants.TEMPLATE_TITLE_BLOG_APPROVED
                : NotificationTemplateConstants.TEMPLATE_CONTENT_BLOG_APPROVED;
            case BLOG_REJECTED -> isTitle
                ? NotificationTemplateConstants.TEMPLATE_TITLE_BLOG_REJECTED
                : NotificationTemplateConstants.TEMPLATE_CONTENT_BLOG_REJECTED;
            case ACHIEVEMENT_UNLOCKED -> isTitle
                ? NotificationTemplateConstants.TEMPLATE_TITLE_ACHIEVEMENT_UNLOCKED
                : NotificationTemplateConstants.TEMPLATE_CONTENT_ACHIEVEMENT_UNLOCKED;
            case FOLLOWED_YOU -> isTitle
                ? NotificationTemplateConstants.TEMPLATE_TITLE_FOLLOWED_YOU
                : NotificationTemplateConstants.TEMPLATE_CONTENT_FOLLOWED_YOU;
            case SYSTEM_ANNOUNCEMENT -> isTitle
                ? NotificationTemplateConstants.TEMPLATE_TITLE_SYSTEM_ANNOUNCEMENT
                : NotificationTemplateConstants.TEMPLATE_CONTENT_SYSTEM_ANNOUNCEMENT;
        };
        
        return formatTemplate(template, args);
    }
}


