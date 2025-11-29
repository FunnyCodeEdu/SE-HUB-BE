package com.se.hub.modules.interaction.constant;

/**
 * Interaction Message Constants
 * Contains all message strings for Interaction module
 */
public class InteractionMessageConstants {
    // Error Messages - Comments
    public static final String COMMENT_NOT_FOUND_MESSAGE = "Comment not found";
    public static final String COMMENT_AUTHOR_INVALID_MESSAGE = "Comment author is invalid";
    public static final String COMMENT_CONTENT_INVALID_MESSAGE = "Comment content is invalid";
    public static final String COMMENT_TARGET_TYPE_INVALID_MESSAGE = "Comment target type is invalid";
    public static final String COMMENT_TARGET_ID_INVALID_MESSAGE = "Comment target ID is invalid";
    public static final String COMMENT_ID_REQUIRED_MESSAGE = "Comment ID is required";
    public static final String FORBIDDEN_OPERATION_MESSAGE = "You are not allowed to perform this operation";

    // Error Messages - Reactions
    public static final String REACTION_ERROR_MESSAGE = "Reaction error occurred";
    public static final String REACTION_NOT_FOUND_MESSAGE = "Reaction not found";

    // Error Messages - Reports
    public static final String REPORT_NOT_FOUND_MESSAGE = "Report not found";
    public static final String REPORT_ERROR_MESSAGE = "Report error occurred";
    public static final String REPORT_ALREADY_EXISTS_MESSAGE = "You have already reported this target";
    public static final String REPORT_DELETE_FORBIDDEN_MESSAGE = "You are not allowed to delete this report";

    // API Response Messages - Comments
    public static final String API_COMMENT_CREATED_SUCCESS = "Comment created successfully";
    public static final String API_COMMENT_RETRIEVED_ALL_SUCCESS = "Retrieved all comments successfully";
    public static final String API_COMMENT_RETRIEVED_BY_ID_SUCCESS = "Retrieved comment by ID successfully";
    public static final String API_COMMENT_RETRIEVED_BY_TARGET_SUCCESS = "Retrieved comments by target successfully";
    public static final String API_COMMENT_UPDATED_SUCCESS = "Comment updated successfully";
    public static final String API_COMMENT_DELETED_SUCCESS = "Comment deleted successfully";

    // API Response Messages - Reactions
    public static final String API_REACTION_ADDED_SUCCESS = "Reaction added successfully";
    public static final String API_REACTION_REMOVED_SUCCESS = "Reaction removed successfully";
    public static final String API_REACTION_COUNT_RETRIEVED_SUCCESS = "Reaction count retrieved successfully";
    public static final String API_REACTION_RETRIEVED_ALL_SUCCESS = "Retrieved all reactions successfully";
    public static final String API_REACTION_RETRIEVED_BY_TARGET_TYPE_SUCCESS = "Retrieved reactions by target type successfully";

    // API Response Messages - Reports
    public static final String API_REPORT_CREATED_SUCCESS = "Report created successfully";
    public static final String API_REPORT_RETRIEVED_ALL_SUCCESS = "Retrieved all reports successfully";
    public static final String API_REPORT_RETRIEVED_BY_ID_SUCCESS = "Retrieved report by ID successfully";
    public static final String API_REPORT_RETRIEVED_BY_TARGET_SUCCESS = "Retrieved reports by target successfully";
    public static final String API_REPORT_RETRIEVED_BY_STATUS_SUCCESS = "Retrieved reports by status successfully";
    public static final String API_REPORT_RETRIEVED_MY_REPORTS_SUCCESS = "Retrieved my reports successfully";
    public static final String API_REPORT_UPDATED_SUCCESS = "Report updated successfully";
    public static final String API_REPORT_DELETED_SUCCESS = "Report deleted successfully";
    public static final String API_REPORT_CHECK_SUCCESS = "Report check completed successfully";
    public static final String API_REPORT_SUMMARY_RETRIEVED_SUCCESS = "Report summary retrieved successfully";

    // Common API Response Messages
    public static final String API_BAD_REQUEST = "Bad request";
    public static final String API_INTERNAL_ERROR = "Internal server error";
    public static final String INVALID_REQUEST_PARAMETERS = "Invalid request parameters";
    public static final String INVALID_REQUEST_BODY = "Invalid request body";

    private InteractionMessageConstants() {
        // Prevent instantiation
    }
}

