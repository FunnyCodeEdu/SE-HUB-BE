package com.se.hub.modules.exam.exception;

import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.modules.exam.constant.AnswerReportMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

/**
 * Answer Report Error Code Enum
 * Defines all error codes specific to Answer Report module with formatMessage support
 */
@Getter
@AllArgsConstructor
public enum AnswerReportErrorCode {
    ANSWER_REPORT_NOT_FOUND(MessageCodeConstant.E002_NOT_FOUND, AnswerReportMessageConstants.ANSWER_REPORT_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    ANSWER_REPORT_ID_REQUIRED(MessageCodeConstant.E001_VALIDATION_ERROR, "Answer report ID is required", HttpStatus.BAD_REQUEST),
    ANSWER_REPORT_QUESTION_ID_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, "Question ID is required", HttpStatus.BAD_REQUEST),
    ANSWER_REPORT_QUESTION_OPTION_ID_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, "Question Option ID is required", HttpStatus.BAD_REQUEST),
    ANSWER_REPORT_DESCRIPTION_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, "Description must not exceed 1000 characters", HttpStatus.BAD_REQUEST),
    ANSWER_REPORT_SUGGESTED_ANSWER_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, "Suggested correct answer must not exceed 2000 characters", HttpStatus.BAD_REQUEST),
    ANSWER_REPORT_FORBIDDEN_OPERATION(MessageCodeConstant.E004_FORBIDDEN, "You do not have permission to perform this operation", HttpStatus.FORBIDDEN),
    ANSWER_REPORT_ALREADY_PROCESSED(MessageCodeConstant.E001_VALIDATION_ERROR, "This report has already been processed", HttpStatus.BAD_REQUEST);

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
            case ANSWER_REPORT_NOT_FOUND -> ErrorCode.DATA_NOT_FOUND;
            case ANSWER_REPORT_ID_REQUIRED, ANSWER_REPORT_QUESTION_ID_INVALID, ANSWER_REPORT_QUESTION_OPTION_ID_INVALID,
                 ANSWER_REPORT_DESCRIPTION_INVALID, ANSWER_REPORT_SUGGESTED_ANSWER_INVALID, ANSWER_REPORT_ALREADY_PROCESSED -> ErrorCode.DATA_INVALID;
            case ANSWER_REPORT_FORBIDDEN_OPERATION -> ErrorCode.AUTHZ_UNAUTHORIZED;
        };
    }

    /**
     * Create AnswerReportException with formatted message
     *
     * @param args arguments to format message
     * @return AnswerReportException instance
     */
    public AnswerReportException toException(Object... args) {
        return new AnswerReportException(this, args);
    }
}

