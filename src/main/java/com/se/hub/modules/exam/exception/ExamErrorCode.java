package com.se.hub.modules.exam.exception;

import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.modules.exam.constant.ExamMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

/**
 * Exam Error Code Enum
 * Defines all error codes specific to Exam module with formatMessage support
 */
@Getter
@AllArgsConstructor
public enum ExamErrorCode {
    EXAM_NOT_FOUND(MessageCodeConstant.E002_NOT_FOUND, ExamMessageConstants.EXAM_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    EXAM_CODE_EXISTED(MessageCodeConstant.E001_VALIDATION_ERROR, ExamMessageConstants.EXAM_CODE_EXISTED_MESSAGE, HttpStatus.CONFLICT),
    EXAM_ID_REQUIRED(MessageCodeConstant.E001_VALIDATION_ERROR, ExamMessageConstants.EXAM_ID_REQUIRED_MESSAGE, HttpStatus.BAD_REQUEST),
    EXAM_QUESTIONS_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, ExamMessageConstants.EXAM_QUESTIONS_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    EXAM_TITLE_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, ExamMessageConstants.EXAM_TITLE_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    EXAM_DESCRIPTION_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, ExamMessageConstants.EXAM_DESCRIPTION_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    EXAM_DURATION_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, ExamMessageConstants.EXAM_DURATION_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    EXAM_TYPE_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, ExamMessageConstants.EXAM_TYPE_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    EXAM_CODE_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, ExamMessageConstants.EXAM_CODE_INVALID_MESSAGE, HttpStatus.BAD_REQUEST);

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
            case EXAM_NOT_FOUND -> ErrorCode.DATA_NOT_FOUND;
            case EXAM_CODE_EXISTED -> ErrorCode.DATA_EXISTED;
            case EXAM_ID_REQUIRED, EXAM_QUESTIONS_INVALID, EXAM_TITLE_INVALID, 
                 EXAM_DESCRIPTION_INVALID, EXAM_DURATION_INVALID, EXAM_TYPE_INVALID, 
                 EXAM_CODE_INVALID -> ErrorCode.DATA_INVALID;
        };
    }

    /**
     * Create ExamException with formatted message
     *
     * @param args arguments to format message
     * @return ExamException instance
     */
    public ExamException toException(Object... args) {
        return new ExamException(this, args);
    }
}

