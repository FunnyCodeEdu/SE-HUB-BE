package com.se.hub.modules.blog.exception;

import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.modules.blog.constant.BlogMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

/**
 * Blog Error Code Enum
 * Defines all error codes specific to Blog module with formatMessage support
 */
@Getter
@AllArgsConstructor
public enum BlogErrorCode {
    BLOG_NOT_FOUND(MessageCodeConstant.E002_NOT_FOUND, BlogMessageConstants.BLOG_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    BLOG_AUTHOR_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, BlogMessageConstants.BLOG_AUTHOR_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    BLOG_CONTENT_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, BlogMessageConstants.BLOG_CONTENT_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    BLOG_ALLOW_COMMENTS_INVALID(MessageCodeConstant.E001_VALIDATION_ERROR, BlogMessageConstants.BLOG_ALLOW_COMMENTS_INVALID_MESSAGE, HttpStatus.BAD_REQUEST),
    BLOG_ID_REQUIRED(MessageCodeConstant.E001_VALIDATION_ERROR, BlogMessageConstants.BLOG_ID_REQUIRED_MESSAGE, HttpStatus.BAD_REQUEST),
    BLOG_ALREADY_APPROVED(MessageCodeConstant.E001_VALIDATION_ERROR, BlogMessageConstants.BLOG_ALREADY_APPROVED_MESSAGE, HttpStatus.BAD_REQUEST),
    BLOG_ALREADY_REJECTED(MessageCodeConstant.E001_VALIDATION_ERROR, BlogMessageConstants.BLOG_ALREADY_REJECTED_MESSAGE, HttpStatus.BAD_REQUEST),
    BLOG_FORBIDDEN_OPERATION(MessageCodeConstant.E004_FORBIDDEN, BlogMessageConstants.BLOG_FORBIDDEN_OPERATION_MESSAGE, HttpStatus.FORBIDDEN),
    BLOG_REACTION_ALREADY_EXISTS(MessageCodeConstant.E001_VALIDATION_ERROR, BlogMessageConstants.BLOG_REACTION_ALREADY_EXISTS_MESSAGE, HttpStatus.BAD_REQUEST);

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
            case BLOG_NOT_FOUND -> ErrorCode.DATA_NOT_FOUND;
            case BLOG_AUTHOR_INVALID, BLOG_CONTENT_INVALID, BLOG_ALLOW_COMMENTS_INVALID, BLOG_ID_REQUIRED,
                 BLOG_ALREADY_APPROVED, BLOG_ALREADY_REJECTED, BLOG_REACTION_ALREADY_EXISTS ->
                    ErrorCode.DATA_INVALID;
            case BLOG_FORBIDDEN_OPERATION -> ErrorCode.AUTHZ_UNAUTHORIZED;
        };
    }

    /**
     * Create BlogException with formatted message
     *
     * @param args arguments to format message
     * @return BlogException instance
     */
    public BlogException toException(Object... args) {
        return new BlogException(this, args);
    }
}

