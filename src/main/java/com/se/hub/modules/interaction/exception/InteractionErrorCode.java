package com.se.hub.modules.interaction.exception;

import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.modules.interaction.constant.InteractionMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

/**
 * Interaction Error Code Enum
 * Defines all error codes specific to Interaction module with formatMessage support
 */
@Getter
@AllArgsConstructor
public enum InteractionErrorCode {
    COMMENT_NOT_FOUND(MessageCodeConstant.E002_NOT_FOUND, InteractionMessageConstants.COMMENT_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    COMMENT_AUTHOR_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, InteractionMessageConstants.COMMENT_AUTHOR_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    COMMENT_CONTENT_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, InteractionMessageConstants.COMMENT_CONTENT_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    COMMENT_TARGET_TYPE_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, InteractionMessageConstants.COMMENT_TARGET_TYPE_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    COMMENT_TARGET_ID_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, InteractionMessageConstants.COMMENT_TARGET_ID_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    COMMENT_ID_REQUIRED(MessageCodeConstant.E001_VALIDATION_ERROR, InteractionMessageConstants.COMMENT_ID_REQUIRED_MESSAGE, HttpStatus.BAD_REQUEST),
    REACTION_ERROR(MessageCodeConstant.E001_VALIDATION_ERROR, InteractionMessageConstants.REACTION_ERROR_MESSAGE, HttpStatus.BAD_REQUEST),
    REACTION_NOT_FOUND(MessageCodeConstant.E002_NOT_FOUND, InteractionMessageConstants.REACTION_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    REPORT_NOT_FOUND(MessageCodeConstant.E002_NOT_FOUND, InteractionMessageConstants.REPORT_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    REPORT_ERROR(MessageCodeConstant.E001_VALIDATION_ERROR, InteractionMessageConstants.REPORT_ERROR_MESSAGE, HttpStatus.BAD_REQUEST),
    REPORT_ALREADY_EXISTS(MessageCodeConstant.E001_VALIDATION_ERROR, InteractionMessageConstants.REPORT_ALREADY_EXISTS_MESSAGE, HttpStatus.BAD_REQUEST),
    REPORT_DELETE_FORBIDDEN(MessageCodeConstant.E004_FORBIDDEN, InteractionMessageConstants.REPORT_DELETE_FORBIDDEN_MESSAGE, HttpStatus.FORBIDDEN),
    FORBIDDEN_OPERATION(MessageCodeConstant.E004_FORBIDDEN, InteractionMessageConstants.FORBIDDEN_OPERATION_MESSAGE, HttpStatus.FORBIDDEN);

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
            case COMMENT_NOT_FOUND, REACTION_NOT_FOUND, REPORT_NOT_FOUND -> ErrorCode.DATA_NOT_FOUND;
            case COMMENT_AUTHOR_INVALID, COMMENT_CONTENT_INVALID, COMMENT_TARGET_TYPE_INVALID,
                 COMMENT_TARGET_ID_INVALID, COMMENT_ID_REQUIRED, REACTION_ERROR, REPORT_ERROR,
                 REPORT_ALREADY_EXISTS -> ErrorCode.DATA_INVALID;
            case FORBIDDEN_OPERATION, REPORT_DELETE_FORBIDDEN -> ErrorCode.AUTHZ_UNAUTHORIZED;
        };
    }

    /**
     * Create InteractionException with formatted message
     *
     * @param args arguments to format message
     * @return InteractionException instance
     */
    public InteractionException toException(Object... args) {
        return new InteractionException(this, args);
    }
}

