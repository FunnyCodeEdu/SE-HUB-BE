package com.se.hub.modules.course.exception;

import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.modules.course.constant.CourseMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

/**
 * Course Error Code Enum
 * Defines all error codes specific to Course module with formatMessage support
 */
@Getter
@AllArgsConstructor
public enum CourseErrorCode {
    COURSE_NOT_FOUND(MessageCodeConstant.E002_NOT_FOUND, CourseMessageConstants.COURSE_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    COURSE_NAME_EXISTED(MessageCodeConstant.E001_VALIDATION_ERROR, CourseMessageConstants.COURSE_NAME_EXISTED_MESSAGE, HttpStatus.CONFLICT),
    COURSE_NAME_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, CourseMessageConstants.COURSE_NAME_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    COURSE_DESCRIPTION_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, CourseMessageConstants.COURSE_DESCRIPTION_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    COURSE_SEMESTER_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, CourseMessageConstants.COURSE_SEMESTER_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    COURSE_SPECIALIZATION_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, CourseMessageConstants.COURSE_SPECIALIZATION_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    COURSE_ID_REQUIRED(MessageCodeConstant.E001_VALIDATION_ERROR, CourseMessageConstants.COURSE_ID_REQUIRED_MESSAGE, HttpStatus.BAD_REQUEST);

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
            case COURSE_NOT_FOUND -> ErrorCode.DATA_NOT_FOUND;
            case COURSE_NAME_EXISTED -> ErrorCode.DATA_EXISTED;
            case COURSE_NAME_INVALID, COURSE_DESCRIPTION_INVALID, COURSE_SEMESTER_INVALID, 
                 COURSE_SPECIALIZATION_INVALID, COURSE_ID_REQUIRED -> ErrorCode.DATA_INVALID;
        };
    }

    /**
     * Create CourseException with formatted message
     *
     * @param args arguments to format message
     * @return CourseException instance
     */
    public CourseException toException(Object... args) {
        return new CourseException(this, args);
    }
}

