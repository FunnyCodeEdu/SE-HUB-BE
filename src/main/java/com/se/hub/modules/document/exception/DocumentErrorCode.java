package com.se.hub.modules.document.exception;

import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.modules.document.constant.DocumentMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

/**
 * Document Error Code Enum
 * Defines all error codes specific to Document module with formatMessage support
 */
@Getter
@AllArgsConstructor
public enum DocumentErrorCode {
    DOCUMENT_NOT_FOUND(MessageCodeConstant.E002_NOT_FOUND, DocumentMessageConstants.DOCUMENT_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    DOCUMENT_NAME_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, DocumentMessageConstants.DOCUMENT_NAME_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    DOCUMENT_DESCRIPT_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, DocumentMessageConstants.DOCUMENT_DESCRIPT_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    DOCUMENT_SEMESTER_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, DocumentMessageConstants.DOCUMENT_SEMESTER_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    DOCUMENT_MAJOR_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, DocumentMessageConstants.DOCUMENT_MAJOR_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    DOCUMENT_COURSE_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, DocumentMessageConstants.DOCUMENT_COURSE_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    DOCUMENT_ID_REQUIRED(MessageCodeConstant.E001_VALIDATION_ERROR, DocumentMessageConstants.DOCUMENT_ID_REQUIRED_MESSAGE, HttpStatus.BAD_REQUEST),
    DOCUMENT_COURSE_NOT_FOUND(MessageCodeConstant.E002_NOT_FOUND, DocumentMessageConstants.DOCUMENT_COURSE_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    DOCUMENT_UNAPPROVED(MessageCodeConstant.E001_VALIDATION_ERROR, DocumentMessageConstants.DOCUMENT_UNAPPROVED_MESSAGE, HttpStatus.FORBIDDEN),
    DOCUMENT_FILE_REQUIRED(MessageCodeConstant.E001_VALIDATION_ERROR, DocumentMessageConstants.DOCUMENT_FILE_REQUIRED_MESSAGE, HttpStatus.BAD_REQUEST),
    DOCUMENT_UPLOAD_FAILED(MessageCodeConstant.E005_INTERNAL_ERROR, DocumentMessageConstants.DOCUMENT_UPLOAD_FAILED_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR),
    DOCUMENT_IMAGE_INVALID_FORMAT(MessageCodeConstant.E001_VALIDATION_ERROR, DocumentMessageConstants.DOCUMENT_IMAGE_INVALID_FORMAT_MESSAGE, HttpStatus.BAD_REQUEST),
    DOCUMENT_GOOGLE_DRIVE_NOT_CONFIGURED(MessageCodeConstant.E001_VALIDATION_ERROR, DocumentMessageConstants.DOCUMENT_GOOGLE_DRIVE_NOT_CONFIGURED_MESSAGE, HttpStatus.BAD_REQUEST),
    DOCUMENT_FORBIDDEN_OPERATION(MessageCodeConstant.E004_FORBIDDEN, DocumentMessageConstants.DOCUMENT_FORBIDDEN_OPERATION_MESSAGE, HttpStatus.FORBIDDEN),
    DOCUMENT_ALREADY_APPROVED(MessageCodeConstant.E001_VALIDATION_ERROR, DocumentMessageConstants.DOCUMENT_ALREADY_APPROVED_MESSAGE, HttpStatus.BAD_REQUEST);

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
            case DOCUMENT_NOT_FOUND, DOCUMENT_COURSE_NOT_FOUND -> ErrorCode.DATA_NOT_FOUND;
            case DOCUMENT_NAME_INVALID, DOCUMENT_DESCRIPT_INVALID, DOCUMENT_SEMESTER_INVALID,
                 DOCUMENT_MAJOR_INVALID, DOCUMENT_COURSE_INVALID, DOCUMENT_ID_REQUIRED,
                 DOCUMENT_FILE_REQUIRED, DOCUMENT_GOOGLE_DRIVE_NOT_CONFIGURED, DOCUMENT_ALREADY_APPROVED,
                 DOCUMENT_IMAGE_INVALID_FORMAT -> ErrorCode.DATA_INVALID;
            case DOCUMENT_UNAPPROVED -> ErrorCode.DATA_INVALID;
            case DOCUMENT_FORBIDDEN_OPERATION -> ErrorCode.AUTHZ_UNAUTHORIZED;
            case DOCUMENT_UPLOAD_FAILED -> ErrorCode.SERVER_UNCATEGORIZED_EXCEPTION;
        };
    }

    /**
     * Create DocumentException with formatted message
     *
     * @param args arguments to format message
     * @return DocumentException instance
     */
    public DocumentException toException(Object... args) {
        return new DocumentException(this, args);
    }
}

