package com.se.hub.modules.exam.exception;

import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.modules.exam.constant.QuestionOptionMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

/**
 * QuestionOption Error Code Enum
 * Defines all error codes specific to QuestionOption module with formatMessage support
 */
@Getter
@AllArgsConstructor
public enum QuestionOptionErrorCode {
    QUESTION_OPTION_NOT_FOUND(MessageCodeConstant.E002_NOT_FOUND, QuestionOptionMessageConstants.QUESTION_OPTION_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    QUESTION_OPTION_ID_REQUIRED(MessageCodeConstant.E001_VALIDATION_ERROR, QuestionOptionMessageConstants.QUESTION_OPTION_ID_REQUIRED_MESSAGE, HttpStatus.BAD_REQUEST),
    QUESTION_OPTION_CONTENT_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, QuestionOptionMessageConstants.QUESTION_OPTION_CONTENT_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    QUESTION_OPTION_ORDER_INDEX_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, QuestionOptionMessageConstants.QUESTION_OPTION_ORDER_INDEX_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    QUESTION_OPTION_IS_CORRECT_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, QuestionOptionMessageConstants.QUESTION_OPTION_IS_CORRECT_INVALID_MESSAGE, HttpStatus.BAD_REQUEST);

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
            case QUESTION_OPTION_NOT_FOUND -> ErrorCode.DATA_NOT_FOUND;
            case QUESTION_OPTION_ID_REQUIRED, QUESTION_OPTION_CONTENT_INVALID, 
                 QUESTION_OPTION_ORDER_INDEX_INVALID, QUESTION_OPTION_IS_CORRECT_INVALID -> ErrorCode.DATA_INVALID;
        };
    }

    /**
     * Create QuestionOptionException with formatted message
     *
     * @param args arguments to format message
     * @return QuestionOptionException instance
     */
    public QuestionOptionException toException(Object... args) {
        return new QuestionOptionException(this, args);
    }
}

