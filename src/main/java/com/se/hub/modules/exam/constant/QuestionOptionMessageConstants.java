package com.se.hub.modules.exam.constant;

/**
 * QuestionOption Message Constants
 * Contains all message strings for QuestionOption module
 */
public class QuestionOptionMessageConstants {
    // Error Messages
    public static final String QUESTION_OPTION_NOT_FOUND_MESSAGE = "Question option not found";
    public static final String QUESTION_OPTION_ID_REQUIRED_MESSAGE = "Question option ID is required";
    public static final String QUESTION_OPTION_CONTENT_INVALID_MESSAGE = "Question option content is invalid";
    public static final String QUESTION_OPTION_ORDER_INDEX_INVALID_MESSAGE = "Question option order index is invalid";
    public static final String QUESTION_OPTION_IS_CORRECT_INVALID_MESSAGE = "Question option isCorrect is invalid";

    // API Response Messages
    public static final String API_QUESTION_OPTION_RETRIEVED_BY_ID_SUCCESS = "Retrieved question option by ID successfully";
    public static final String API_QUESTION_OPTION_RETRIEVED_BY_QUESTION_SUCCESS = "Retrieved question options by question ID successfully";
    public static final String API_QUESTION_OPTION_RETRIEVED_CORRECT_SUCCESS = "Retrieved correct question options successfully";
    public static final String API_QUESTION_OPTION_UPDATED_SUCCESS = "Question option updated successfully";
    public static final String API_QUESTION_OPTION_DELETED_SUCCESS = "Question option deleted successfully";
    public static final String API_QUESTION_OPTIONS_DELETED_SUCCESS = "Question options deleted successfully";
    public static final String API_BAD_REQUEST = "Bad request";
    public static final String API_INTERNAL_ERROR = "Internal server error";

    private QuestionOptionMessageConstants() {
        // Prevent instantiation
    }
}

