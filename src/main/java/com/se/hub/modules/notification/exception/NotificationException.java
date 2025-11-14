package com.se.hub.modules.notification.exception;

import com.se.hub.common.exception.AppException;
import lombok.Getter;

/**
 * Custom exception for Notification module
 * Extends AppException to provide notification-specific error handling
 */
@Getter
public class NotificationException extends AppException {
    private final NotificationErrorCode notificationErrorCode;

    public NotificationException(NotificationErrorCode notificationErrorCode, Object... args) {
        super(notificationErrorCode.toErrorCode());
        this.notificationErrorCode = notificationErrorCode;
    }
}



