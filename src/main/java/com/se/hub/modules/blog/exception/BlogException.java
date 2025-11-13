package com.se.hub.modules.blog.exception;

import com.se.hub.common.exception.AppException;
import lombok.Getter;

/**
 * Custom exception for Blog module
 * Extends AppException to provide blog-specific error handling
 */
@Getter
public class BlogException extends AppException {
    private final BlogErrorCode blogErrorCode;

    public BlogException(BlogErrorCode blogErrorCode, Object... args) {
        super(blogErrorCode.toErrorCode());
        this.blogErrorCode = blogErrorCode;
    }
}

