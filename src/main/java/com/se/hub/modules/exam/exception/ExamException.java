package com.se.hub.modules.exam.exception;

import com.se.hub.common.exception.AppException;
import lombok.Getter;

/**
 * Custom exception for Exam module
 * Extends AppException to provide exam-specific error handling
 */
@Getter
public class ExamException extends AppException {
    private final ExamErrorCode examErrorCode;

    public ExamException(ExamErrorCode examErrorCode, Object... args) {
        super(examErrorCode.toErrorCode());
        this.examErrorCode = examErrorCode;
    }
}

