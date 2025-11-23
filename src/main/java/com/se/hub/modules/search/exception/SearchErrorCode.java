package com.se.hub.modules.search.exception;

import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.modules.search.constant.SearchMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SearchErrorCode {
    KEYWORD_REQUIRED(MessageCodeConstant.E001_VALIDATION_ERROR,
            SearchMessageConstants.SEARCH_KEYWORD_REQUIRED_MESSAGE, HttpStatus.BAD_REQUEST),
    TARGET_REQUIRED(MessageCodeConstant.E001_VALIDATION_ERROR,
            SearchMessageConstants.SEARCH_TARGET_REQUIRED_MESSAGE, HttpStatus.BAD_REQUEST),
    TARGET_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR,
            SearchMessageConstants.SEARCH_TARGET_INVALID_MESSAGE, HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    public ErrorCode toErrorCode() {
        return ErrorCode.DATA_INVALID;
    }

    public SearchException toException() {
        return new SearchException(this);
    }
}



