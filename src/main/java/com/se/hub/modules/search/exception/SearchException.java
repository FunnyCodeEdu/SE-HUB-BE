package com.se.hub.modules.search.exception;

import com.se.hub.common.exception.AppException;
import lombok.Getter;

@Getter
public class SearchException extends AppException {
    private final SearchErrorCode searchErrorCode;

    public SearchException(SearchErrorCode searchErrorCode) {
        super(searchErrorCode.toErrorCode());
        this.searchErrorCode = searchErrorCode;
    }
}


