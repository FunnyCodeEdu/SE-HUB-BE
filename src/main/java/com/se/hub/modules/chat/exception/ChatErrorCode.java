package com.se.hub.modules.chat.exception;

import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.modules.chat.constant.ChatMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

/**
 * Chat Error Code Enum
 * Defines all error codes specific to Chat module with formatMessage support
 */
@Getter
@AllArgsConstructor
public enum ChatErrorCode {
    CONVERSATION_NOT_FOUND(MessageCodeConstant.E002_NOT_FOUND, ChatMessageConstants.CONVERSATION_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    NOT_PARTICIPANT(MessageCodeConstant.E004_FORBIDDEN, ChatMessageConstants.NOT_PARTICIPANT_MESSAGE, HttpStatus.FORBIDDEN),
    CONVERSATION_ALREADY_EXISTS(MessageCodeConstant.E001_VALIDATION_ERROR, ChatMessageConstants.CONVERSATION_ALREADY_EXISTS_MESSAGE, HttpStatus.BAD_REQUEST),
    INVALID_PARTICIPANT_COUNT(MessageCodeConstant.E001_VALIDATION_ERROR, ChatMessageConstants.INVALID_PARTICIPANT_COUNT_MESSAGE, HttpStatus.BAD_REQUEST),
    MESSAGE_TOO_LONG(MessageCodeConstant.E001_VALIDATION_ERROR, ChatMessageConstants.MESSAGE_TOO_LONG_MESSAGE, HttpStatus.BAD_REQUEST),
    SESSION_NOT_FOUND(MessageCodeConstant.E002_NOT_FOUND, ChatMessageConstants.SESSION_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    GENERATION_ERROR(MessageCodeConstant.E005_INTERNAL_ERROR, ChatMessageConstants.GENERATION_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);

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
            case CONVERSATION_NOT_FOUND, SESSION_NOT_FOUND -> ErrorCode.DATA_NOT_FOUND;
            case CONVERSATION_ALREADY_EXISTS, INVALID_PARTICIPANT_COUNT, MESSAGE_TOO_LONG -> ErrorCode.DATA_INVALID;
            case NOT_PARTICIPANT -> ErrorCode.AUTHZ_UNAUTHORIZED;
            case GENERATION_ERROR -> ErrorCode.SERVER_UNCATEGORIZED_EXCEPTION;
        };
    }

    /**
     * Create ChatException with formatted message
     *
     * @param args arguments to format message
     * @return ChatException instance
     */
    public ChatException toException(Object... args) {
        return new ChatException(this, args);
    }
}

