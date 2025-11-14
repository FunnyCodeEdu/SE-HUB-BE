package com.se.hub.modules.exam.exception;

import com.se.hub.common.exception.AppException;
import lombok.Getter;

/**
 * Custom exception for Question module
 * Extends AppException to provide question-specific error handling
 */
@Getter
public class QuestionException extends AppException {
    private final QuestionErrorCode questionErrorCode;

    public QuestionException(QuestionErrorCode questionErrorCode, Object... args) {
        super(questionErrorCode.toErrorCode());
        this.questionErrorCode = questionErrorCode;
    }
}

