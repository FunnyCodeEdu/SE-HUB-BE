package com.se.hub.modules.notification.exception;

import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.modules.notification.constant.NotificationMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

/**
 * Notification Error Code Enum
 * Defines all error codes specific to Notification module with formatMessage support
 */
@Getter
@AllArgsConstructor
public enum NotificationErrorCode {
    NOTIFICATION_NOT_FOUND(MessageCodeConstant.E002_NOT_FOUND, NotificationMessageConstants.NOTIFICATION_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    USER_NOTIFICATION_NOT_FOUND(MessageCodeConstant.E002_NOT_FOUND, NotificationMessageConstants.USER_NOTIFICATION_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    NOTIFICATION_TYPE_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, NotificationMessageConstants.NOTIFICATION_TYPE_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    NOTIFICATION_STATUS_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, NotificationMessageConstants.NOTIFICATION_STATUS_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    NOTIFICATION_ID_REQUIRED(MessageCodeConstant.E001_VALIDATION_ERROR, NotificationMessageConstants.NOTIFICATION_ID_REQUIRED_MESSAGE, HttpStatus.BAD_REQUEST),
    USER_ID_REQUIRED(MessageCodeConstant.E001_VALIDATION_ERROR, NotificationMessageConstants.USER_ID_REQUIRED_MESSAGE, HttpStatus.BAD_REQUEST),
    TEMPLATE_NOT_FOUND(MessageCodeConstant.E002_NOT_FOUND, NotificationMessageConstants.TEMPLATE_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND);

    private final String code;
    private final String messageTemplate;
    private final HttpStatus httpStatus;

    /**
     * Format message with arguments
     *
     * @param args arguments to format message
     * @return formatted message
     */
    public String formatMessage(Object... args) {
        if (args == null || args.length == 0) {
            return messageTemplate;
        }
        try {
            return MessageFormat.format(messageTemplate, args);
        } catch (Exception e) {
            return messageTemplate;
        }
    }

    /**
     * Convert to common ErrorCode enum
     *
     * @return ErrorCode enum value
     */
    public ErrorCode toErrorCode() {
        return switch (this) {
            case NOTIFICATION_NOT_FOUND, USER_NOTIFICATION_NOT_FOUND, TEMPLATE_NOT_FOUND -> ErrorCode.DATA_NOT_FOUND;
            case NOTIFICATION_TYPE_INVALID, NOTIFICATION_STATUS_INVALID, NOTIFICATION_ID_REQUIRED, USER_ID_REQUIRED ->
                    ErrorCode.DATA_INVALID;
        };
    }

    /**
     * Create NotificationException with formatted message
     *
     * @param args arguments to format message
     * @return NotificationException instance
     */
    public NotificationException toException(Object... args) {
        return new NotificationException(this, args);
    }
}


