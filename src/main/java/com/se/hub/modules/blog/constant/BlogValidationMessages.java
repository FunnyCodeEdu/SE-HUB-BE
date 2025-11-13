package com.se.hub.modules.blog.constant;

/**
 * Blog Validation Messages
 * Contains validation messages for Blog module
 */
public class BlogValidationMessages {
    // Author validation
    public static final String BLOG_AUTHOR_NOT_NULL = "Blog author cannot be null";
    public static final String BLOG_AUTHOR_INVALID = "Blog author is invalid";

    // Content validation
    public static final String BLOG_CONTENT_NOT_BLANK = "Blog content cannot be blank";
    public static final String BLOG_CONTENT_SIZE_MAX = "Blog content cannot exceed {0} characters";
    public static final String BLOG_CONTENT_INVALID = "Blog content is invalid";

    // Allow comments validation
    public static final String BLOG_ALLOW_COMMENTS_NOT_NULL = "Blog allow comments cannot be null";
    public static final String BLOG_ALLOW_COMMENTS_INVALID = "Blog allow comments is invalid";

    // ID validation
    public static final String BLOG_ID_REQUIRED = "Blog ID is required";
    public static final String BLOG_ID_NOT_BLANK = "Blog ID cannot be blank";

    // Cover image validation
    public static final String BLOG_COVER_IMAGE_URL_INVALID = "Blog cover image URL is invalid";
    public static final String BLOG_COVER_IMAGE_URL_SIZE_MAX = "Blog cover image URL cannot exceed {0} characters";

    private BlogValidationMessages() {
        // Prevent instantiation
    }
}

