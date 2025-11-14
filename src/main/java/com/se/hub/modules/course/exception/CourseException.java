package com.se.hub.modules.course.exception;

import com.se.hub.common.exception.AppException;
import lombok.Getter;

/**
 * Custom exception for Course module
 * Extends AppException to provide course-specific error handling
 */
@Getter
public class CourseException extends AppException {
    private final CourseErrorCode courseErrorCode;

    public CourseException(CourseErrorCode courseErrorCode, Object... args) {
        super(courseErrorCode.toErrorCode());
        this.courseErrorCode = courseErrorCode;
    }
}

