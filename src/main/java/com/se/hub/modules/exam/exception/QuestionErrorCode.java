package com.se.hub.modules.exam.exception;

import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.modules.exam.constant.QuestionMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

/**
 * Question Error Code Enum
 * Defines all error codes specific to Question module with formatMessage support
 */
@Getter
@AllArgsConstructor
public enum QuestionErrorCode {
    QUESTION_NOT_FOUND(MessageCodeConstant.E002_NOT_FOUND, QuestionMessageConstants.QUESTION_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    QUESTION_ID_REQUIRED(MessageCodeConstant.E001_VALIDATION_ERROR, QuestionMessageConstants.QUESTION_ID_REQUIRED_MESSAGE, HttpStatus.BAD_REQUEST),
    QUESTION_CONTENT_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, QuestionMessageConstants.QUESTION_CONTENT_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    QUESTION_TYPE_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, QuestionMessageConstants.QUESTION_TYPE_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    QUESTION_DIFFICULTY_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, QuestionMessageConstants.QUESTION_DIFFICULTY_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    QUESTION_SCORE_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, QuestionMessageConstants.QUESTION_SCORE_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    QUESTION_CATEGORY_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, QuestionMessageConstants.QUESTION_CATEGORY_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    QUESTION_JLPT_LEVEL_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, QuestionMessageConstants.QUESTION_JLPT_LEVEL_INVALID_MESSAGE, HttpStatus.BAD_REQUEST);

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
            case QUESTION_NOT_FOUND -> ErrorCode.DATA_NOT_FOUND;
            case QUESTION_ID_REQUIRED, QUESTION_CONTENT_INVALID, QUESTION_TYPE_INVALID,
                 QUESTION_DIFFICULTY_INVALID, QUESTION_SCORE_INVALID, QUESTION_CATEGORY_INVALID,
                 QUESTION_JLPT_LEVEL_INVALID -> ErrorCode.DATA_INVALID;
        };
    }

    /**
     * Create QuestionException with formatted message
     *
     * @param args arguments to format message
     * @return QuestionException instance
     */
    public QuestionException toException(Object... args) {
        return new QuestionException(this, args);
    }
}

