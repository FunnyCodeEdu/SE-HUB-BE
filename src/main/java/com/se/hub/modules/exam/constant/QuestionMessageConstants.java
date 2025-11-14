package com.se.hub.modules.exam.constant;

/**
 * Question Message Constants
 * Contains all message strings for Question module
 */
public class QuestionMessageConstants {
    // Error Messages
    public static final String QUESTION_NOT_FOUND_MESSAGE = "Question not found";
    public static final String QUESTION_ID_REQUIRED_MESSAGE = "Question ID is required";
    public static final String QUESTION_CONTENT_INVALID_MESSAGE = "Question content is invalid";
    public static final String QUESTION_TYPE_INVALID_MESSAGE = "Question type is invalid";
    public static final String QUESTION_DIFFICULTY_INVALID_MESSAGE = "Question difficulty is invalid";
    public static final String QUESTION_SCORE_INVALID_MESSAGE = "Question score is invalid";
    public static final String QUESTION_CATEGORY_INVALID_MESSAGE = "Question category is invalid";

    // API Response Messages
    public static final String API_QUESTION_CREATED_SUCCESS = "Question created successfully";
    public static final String API_QUESTION_RETRIEVED_ALL_SUCCESS = "Retrieved all questions successfully";
    public static final String API_QUESTION_RETRIEVED_BY_ID_SUCCESS = "Retrieved question by ID successfully";
    public static final String API_QUESTION_RETRIEVED_BY_CATEGORY_SUCCESS = "Retrieved questions by category successfully";
    public static final String API_QUESTION_RETRIEVED_BY_DIFFICULTY_SUCCESS = "Retrieved questions by difficulty successfully";
    public static final String API_QUESTION_RETRIEVED_BY_TYPE_SUCCESS = "Retrieved questions by type successfully";
    public static final String API_QUESTION_SEARCH_SUCCESS = "Search questions successfully";
    public static final String API_QUESTION_RANDOM_SUCCESS = "Retrieved random questions successfully";
    public static final String API_QUESTION_UPDATED_SUCCESS = "Question updated successfully";
    public static final String API_QUESTION_DELETED_SUCCESS = "Question deleted successfully";
    public static final String API_BAD_REQUEST = "Bad request";
    public static final String API_INTERNAL_ERROR = "Internal server error";

    private QuestionMessageConstants() {
        // Prevent instantiation
    }
}

