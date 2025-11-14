package com.se.hub.modules.course.constant;

/**
 * Course Message Constants
 * Contains all message strings for Course module
 */
public class CourseMessageConstants {
    // Error Messages
    public static final String COURSE_NOT_FOUND_MESSAGE = "Course not found";
    public static final String COURSE_NAME_EXISTED_MESSAGE = "Course name already exists";
    public static final String COURSE_NAME_INVALID_MESSAGE = "Course name is invalid";
    public static final String COURSE_DESCRIPTION_INVALID_MESSAGE = "Course description is invalid";
    public static final String COURSE_SEMESTER_INVALID_MESSAGE = "Course semester is invalid";
    public static final String COURSE_SPECIALIZATION_INVALID_MESSAGE = "Course specialization is invalid";
    public static final String COURSE_ID_REQUIRED_MESSAGE = "Course ID is required";

    // API Response Messages
    public static final String API_COURSE_CREATED_SUCCESS = "Course created successfully";
    public static final String API_COURSE_RETRIEVED_ALL_SUCCESS = "Retrieved all courses successfully";
    public static final String API_COURSE_RETRIEVED_BY_ID_SUCCESS = "Retrieved course by ID successfully";
    public static final String API_COURSE_RETRIEVED_BY_USER_ID_SUCCESS = "Retrieved courses by user ID successfully";
    public static final String API_COURSE_UPDATED_SUCCESS = "Course updated successfully";
    public static final String API_COURSE_DELETED_SUCCESS = "Course deleted successfully";
    public static final String API_BAD_REQUEST = "Bad request";
    public static final String API_INTERNAL_ERROR = "Internal server error";

    private CourseMessageConstants() {
        // Prevent instantiation
    }
}




