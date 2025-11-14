package com.se.hub.modules.document.exception;

import com.se.hub.common.exception.AppException;
import lombok.Getter;

/**
 * Custom exception for Document module
 * Extends AppException to provide document-specific error handling
 */
@Getter
public class DocumentException extends AppException {
    private final DocumentErrorCode documentErrorCode;

    public DocumentException(DocumentErrorCode documentErrorCode, Object... args) {
        super(documentErrorCode.toErrorCode());
        this.documentErrorCode = documentErrorCode;
    }
}

