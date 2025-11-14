package com.se.hub.modules.exam.exception;

import com.se.hub.common.exception.AppException;
import lombok.Getter;

/**
 * Custom exception for QuestionOption module
 * Extends AppException to provide question option-specific error handling
 */
@Getter
public class QuestionOptionException extends AppException {
    private final QuestionOptionErrorCode questionOptionErrorCode;

    public QuestionOptionException(QuestionOptionErrorCode questionOptionErrorCode, Object... args) {
        super(questionOptionErrorCode.toErrorCode());
        this.questionOptionErrorCode = questionOptionErrorCode;
    }
}

