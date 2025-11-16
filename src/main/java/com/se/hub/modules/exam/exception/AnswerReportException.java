package com.se.hub.modules.exam.exception;

import com.se.hub.common.exception.AppException;
import lombok.Getter;

/**
 * Answer Report Exception
 * Custom exception for Answer Report module
 */
@Getter
public class AnswerReportException extends AppException {
    private final AnswerReportErrorCode answerReportErrorCode;

    public AnswerReportException(AnswerReportErrorCode answerReportErrorCode, Object... args) {
        super(answerReportErrorCode.toErrorCode());
        this.answerReportErrorCode = answerReportErrorCode;
    }
}

