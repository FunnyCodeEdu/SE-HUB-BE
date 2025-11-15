package com.se.hub.modules.blog.constant;

/**
 * Blog Message Constants
 * Contains all message strings for Blog module
 */
public class BlogMessageConstants {
    // Error Messages
    public static final String BLOG_NOT_FOUND_MESSAGE = "Blog not found";
    public static final String BLOG_AUTHOR_INVALID_MESSAGE = "Blog author is invalid";
    public static final String BLOG_CONTENT_INVALID_MESSAGE = "Blog content is invalid";
    public static final String BLOG_ALLOW_COMMENTS_INVALID_MESSAGE = "Blog allow comments field is invalid";
    public static final String BLOG_ID_REQUIRED_MESSAGE = "Blog ID is required";
    public static final String BLOG_ALREADY_APPROVED_MESSAGE = "Blog is already approved";
    public static final String BLOG_ALREADY_REJECTED_MESSAGE = "Blog is already rejected";
    public static final String BLOG_FORBIDDEN_OPERATION_MESSAGE = "You do not have permission to perform this operation";
    public static final String BLOG_REACTION_ALREADY_EXISTS_MESSAGE = "You have already reacted to this blog";

    // API Response Messages
    public static final String API_BLOG_CREATED_SUCCESS = "Blog created successfully";
    public static final String API_BLOG_RETRIEVED_ALL_SUCCESS = "Retrieved all blogs successfully";
    public static final String API_BLOG_RETRIEVED_BY_ID_SUCCESS = "Retrieved blog by ID successfully";
    public static final String API_BLOG_RETRIEVED_BY_AUTHOR_SUCCESS = "Retrieved blogs by author ID successfully";
    public static final String API_BLOG_UPDATED_SUCCESS = "Blog updated successfully";
    public static final String API_BLOG_DELETED_SUCCESS = "Blog deleted successfully";
    public static final String API_BLOG_POPULAR_SUCCESS = "Retrieved popular blogs successfully";
    public static final String API_BLOG_MOST_LIKED_SUCCESS = "Retrieved most liked blogs successfully";
    public static final String API_BLOG_LATEST_SUCCESS = "Retrieved latest blogs successfully";
    public static final String API_BLOG_VIEW_INCREMENTED_SUCCESS = "View count incremented successfully";
    public static final String API_BLOG_REACTION_UPDATED_SUCCESS = "Reaction count updated successfully";
    public static final String API_BLOG_LIKED_SUCCESS = "Blog liked successfully";
    public static final String API_BLOG_DISLIKED_SUCCESS = "Blog disliked successfully";
    public static final String API_BLOG_UNREACTED_SUCCESS = "Blog reaction removed successfully";
    public static final String API_BLOG_APPROVED_SUCCESS = "Blog approved successfully";
    public static final String API_BLOG_REJECTED_SUCCESS = "Blog rejected successfully";
    public static final String API_BLOG_PENDING_RETRIEVED_SUCCESS = "Retrieved pending blogs successfully";
    public static final String API_BAD_REQUEST = "Bad request";
    public static final String API_INTERNAL_ERROR = "Internal server error";

    private BlogMessageConstants() {
        // Prevent instantiation
    }
}

