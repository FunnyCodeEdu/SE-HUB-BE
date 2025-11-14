package com.se.hub.modules.exam.constant;

/**
 * Exam Message Constants
 * Contains all message strings for Exam module
 */
public class ExamMessageConstants {
    // Error Messages
    public static final String EXAM_NOT_FOUND_MESSAGE = "Exam not found";
    public static final String EXAM_CODE_EXISTED_MESSAGE = "Exam code already exists";
    public static final String EXAM_ID_REQUIRED_MESSAGE = "Exam ID is required";
    public static final String EXAM_QUESTIONS_INVALID_MESSAGE = "Exam questions are invalid";
    public static final String EXAM_TITLE_INVALID_MESSAGE = "Exam title is invalid";
    public static final String EXAM_DESCRIPTION_INVALID_MESSAGE = "Exam description is invalid";
    public static final String EXAM_DURATION_INVALID_MESSAGE = "Exam duration is invalid";
    public static final String EXAM_TYPE_INVALID_MESSAGE = "Exam type is invalid";
    public static final String EXAM_CODE_INVALID_MESSAGE = "Exam code is invalid";

    // API Response Messages
    public static final String API_EXAM_CREATED_SUCCESS = "Exam created successfully";
    public static final String API_EXAM_RETRIEVED_ALL_SUCCESS = "Retrieved all exams successfully";
    public static final String API_EXAM_RETRIEVED_BY_ID_SUCCESS = "Retrieved exam by ID successfully";
    public static final String API_EXAM_RETRIEVED_BY_COURSE_SUCCESS = "Retrieved exams by course ID successfully";
    public static final String API_EXAM_UPDATED_SUCCESS = "Exam updated successfully";
    public static final String API_EXAM_DELETED_SUCCESS = "Exam deleted successfully";
    public static final String API_EXAM_QUESTIONS_ADDED_SUCCESS = "Questions added to exam successfully";
    public static final String API_EXAM_QUESTIONS_REMOVED_SUCCESS = "Questions removed from exam successfully";
    public static final String API_EXAM_SUBMITTED_SUCCESS = "Exam submitted successfully";
    public static final String API_EXAM_ATTEMPTS_RETRIEVED_SUCCESS = "Retrieved exam attempts successfully";
    public static final String API_EXAM_MY_ATTEMPTS_RETRIEVED_SUCCESS = "Retrieved my exam attempts successfully";
    public static final String API_BAD_REQUEST = "Bad request";
    public static final String API_INTERNAL_ERROR = "Internal server error";

    private ExamMessageConstants() {
        // Prevent instantiation
    }
}

