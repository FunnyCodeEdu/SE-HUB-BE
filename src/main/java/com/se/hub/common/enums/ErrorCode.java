package com.se.hub.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    SERVER_UNCATEGORIZED_EXCEPTION("SERVER_9999", "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_NULL("ERROR_1000", "Data not null", HttpStatus.BAD_REQUEST),
    DATA_NOT_FOUND("ERROR_1001", "Data not found", HttpStatus.NOT_FOUND),
    DATA_EXISTED("ERROR_1002", "Data already exists", HttpStatus.CONFLICT),
    DATA_INVALID("ERROR_1003", "Data invalid", HttpStatus.BAD_REQUEST),

    // ==== AUTHENTICATION ERRORS ====
    AUTH_UNAUTHENTICATED("AUTH_1000", "Unauthenticated", HttpStatus.UNAUTHORIZED),
    AUTH_MISSING_TOKEN("AUTH_1001", "Client missing token", HttpStatus.BAD_REQUEST),
    AUTH_GENERATION_FAIL("AUTH_1002", "Generation JWT fail", HttpStatus.INTERNAL_SERVER_ERROR),

    // ==== AUTHORIZATION ERRORS ====
    AUTHZ_UNAUTHORIZED("AUTHZ_1000", "You do not have permission", HttpStatus.FORBIDDEN),

    // ====  JWT ====
    JWT_CLAIM_MISSING("JWT_1000", "Claim missing", HttpStatus.NOT_ACCEPTABLE),

    // ===== PAGINATION ERRORS =====
    PAGE_NUMBER_INVALID("PAGE_1000", "Page number must be greater than or equal to 0", HttpStatus.BAD_REQUEST),
    PAGE_SIZE_INVALID("PAGE_1001", "Page size must be between 1 and 100", HttpStatus.BAD_REQUEST),

    // ==== ROLE ====
    ROLE_NOT_FOUND("ROLE_1000", "Role not found", HttpStatus.NOT_FOUND),
    ROLE_NOT_EXISTED("ROLE_1001", "Role not existed", HttpStatus.NOT_FOUND),

    // ==== USER ====
    USER_USERNAME_EXISTED("USER_1000", "Username already exists", HttpStatus.CONFLICT),
    USER_NOT_FOUND("USER_1001", "User not found", HttpStatus.NOT_FOUND),
    USER_USERNAME_NOT_FOUND("USER_1002", "Username not found", HttpStatus.NOT_FOUND),
    USER_USERNAME_INVALID("USER_1003", "Username must be at least 5 characters", HttpStatus.BAD_REQUEST),
    USER_PASSWORD_INVALID("USER_1004", "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_EXISTED("USER_1005", "User is already exists", HttpStatus.CONFLICT),
    USER_NOT_EXISTED("USER_1006", "User not existed", HttpStatus.NOT_FOUND),

    //===== PROFILE ======
    PROFILE_NOT_FOUND("PROFILE_1000", "Profile not found", HttpStatus.NOT_FOUND),
    PROFILE_NOT_EXISTED("PROFILE_1001", "Profile not existed", HttpStatus.NOT_FOUND),
    PROFILE_EMAIL_EXISTED("PROFILE_1002", "Email already exists", HttpStatus.CONFLICT),
    PROFILE_PHONE_EXISTED("PROFILE_1003", "Phone number already exists", HttpStatus.CONFLICT),
    PROFILE_ALREADY_EXISTS("PROFILE_1004", "Profile already exists", HttpStatus.CONFLICT),
    PROFILE_ALREADY_VERIFIED("PROFILE_1005", "Profile already verified", HttpStatus.CONFLICT),
    PROFILE_ALREADY_BLOCKED("PROFILE_1006", "Profile already blocked", HttpStatus.CONFLICT),
    PROFILE_NOT_VERIFIED("PROFILE_1007", "Profile not verified", HttpStatus.FORBIDDEN),
    PROFILE_NOT_ACTIVE("PROFILE_1008", "Profile not active", HttpStatus.FORBIDDEN),
    PROFILE_LEVEL_INVALID("PROFILE_1009", "Invalid user level", HttpStatus.BAD_REQUEST),
    PROFILE_UPDATE_FAILED("PROFILE_1010", "Profile update failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PROFILE_DELETE_FAILED("PROFILE_1011", "Profile delete failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PROFILE_VERIFICATION_FAILED("PROFILE_1012", "Profile verification failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PROFILE_BLOCK_FAILED("PROFILE_1013", "Profile block failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PROFILE_UNBLOCK_FAILED("PROFILE_1014", "Profile unblock failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PROFILE_NOT_NULL("PROFILE_1015", "Profile not null", HttpStatus.INTERNAL_SERVER_ERROR),
    PROFILE_FULL_NAME_NOT_BLANK("PROFILE_1016", "Profile full name not null", HttpStatus.BAD_REQUEST),
    PROFILE_FULL_NAME_SIZE_INVALID("PROFILE_1017", "Profile full name size invalid", HttpStatus.BAD_REQUEST),
    PROFILE_PHONE_NUMBER_PATTERN_INVALID("PROFILE_1019", "Phone number pattern invalid", HttpStatus.BAD_REQUEST),
    PROFILE_EMAIL_INVALID_FORMAT("PROFILE_1020", "Email invalid format", HttpStatus.BAD_REQUEST),
    PROFILE_EMAIL_SIZE_INVALID("PROFILE_1021", "Email size invalid", HttpStatus.BAD_REQUEST),
    PROFILE_AVATAR_URL_SIZE_INVALID("PROFILE_1022", "Avatar url size invalid", HttpStatus.BAD_REQUEST),
    PROFILE_GENDER_NOT_NULL("PROFILE_1023", "Gender not null", HttpStatus.BAD_REQUEST),
    PROFILE_USER_NOT_NULL("PROFILE_1024", "User not null", HttpStatus.BAD_REQUEST),

    //====== USER STATS ======
    STATS_POINTS_MIN_VALUE("STATS_1000", "User stats min point invalid", HttpStatus.BAD_REQUEST),
    STATS_EXAMS_DONE_MIN_VALUE("STATS_1001", "User stats min exams done invalid", HttpStatus.BAD_REQUEST),
    STATS_COMMENT_COUNT_MIN_VALUE("STATS_1002", "User stats min comment count invalid", HttpStatus.BAD_REQUEST),
    STATS_DOCS_UPLOADED_MIN_VALUE("STATS_1003", "User stats min docs uploaded invalid", HttpStatus.BAD_REQUEST),
    STATS_BLOGS_UPLOADED_MIN_VALUE("STATS_1004", "User stats min blogs uploaded invalid", HttpStatus.BAD_REQUEST),
    STATS_POSTS_SHARED_MIN_VALUE("STATS_1005", "User stats min posts shared invalid", HttpStatus.BAD_REQUEST),

    //====== USER LEVEL ======
    LEVEL_NOT_NULL("LEVEL_1000", "User level not null", HttpStatus.BAD_REQUEST),
    LEVEL_MIN_POINTS_MIN_VALUE("LEVEL_1001", "User level min points invalid", HttpStatus.BAD_REQUEST),
    LEVEL_MAX_POINTS_MIN_VALUE("LEVEL_1002", "User level max points min value invalid", HttpStatus.BAD_REQUEST),

    //====== ACHIEVEMENT ======
    ACHIEVEMENT_TYPE_NOT_NULL("ACHIEVEMENT_1000", "Achievement type not null", HttpStatus.BAD_REQUEST),
    ACHIEVEMENT_MIN_EXAMS_DONE_MIN_VALUE("ACHIEVEMENT_1001", "Achievement min exams done invalid", HttpStatus.BAD_REQUEST),
    ACHIEVEMENT_MIN_POINTS_MIN_VALUE("ACHIEVEMENT_1002", "Achievement min points invalid", HttpStatus.BAD_REQUEST),
    ACHIEVEMENT_MIN_CMT_COUNT_MIN_VALUE("ACHIEVEMENT_1003", "Achievement min comment count invalid", HttpStatus.BAD_REQUEST),
    ACHIEVEMENT_MIN_DOCS_UPLOADED_MIN_VALUE("ACHIEVEMENT_1004", "Achievement min docs uploaded invalid", HttpStatus.BAD_REQUEST),
    ACHIEVEMENT_MIN_BLOGS_UPLOADED_MIN_VALUE("ACHIEVEMENT_1005", "Achievement min blogs uploaded invalid", HttpStatus.BAD_REQUEST),
    ACHIEVEMENT_MIN_BLOG_SHARED_MIN_VALUE("ACHIEVEMENT_1006", "Achievement min blog shared invalid", HttpStatus.BAD_REQUEST),
    ACHIEVEMENT_TYPE_EXISTED("ACHIEVEMENT_1007", "Achievement type already exists", HttpStatus.CONFLICT),

    //====== COURSE ======
    COURSE_NAME_EXISTED("COURSE_1000", "Course name already exists", HttpStatus.CONFLICT),
    COURSE_NOT_FOUND("COURSE_1001", "Course not found", HttpStatus.NOT_FOUND),
    COURSE_NAME_INVALID("COURSE_1002", "Course name must be between 3 and 100 characters", HttpStatus.BAD_REQUEST),
    COURSE_DESCRIPTION_INVALID("COURSE_1003", "Course description must not exceed 1000 characters", HttpStatus.BAD_REQUEST),
    COURSE_IMG_URL_INVALID("COURSE_1004", "Course image URL is invalid", HttpStatus.BAD_REQUEST),
    COURSE_SEMESTER_INVALID("COURSE_1005", "Course semester must be between 1 and 9", HttpStatus.BAD_REQUEST),
    COURSE_SPECIALIZATION_INVALID("COURSE_1006", "Course specialization is required", HttpStatus.BAD_REQUEST),

    //====== LESSON ======
    LESSON_TITLE_EXISTED("LESSON_1000", "Lesson title already exists", HttpStatus.CONFLICT),
    LESSON_NOT_FOUND("LESSON_1001", "Lesson not found", HttpStatus.NOT_FOUND),
    LESSON_TITLE_INVALID("LESSON_1002", "Lesson title must be between 3 and 100 characters", HttpStatus.BAD_REQUEST),
    LESSON_DESCRIPTION_INVALID("LESSON_1003", "Lesson description must not exceed 1000 characters", HttpStatus.BAD_REQUEST),
    LESSON_TYPE_INVALID("LESSON_1004", "Lesson type is required", HttpStatus.BAD_REQUEST),
    LESSON_PARENT_INVALID("LESSON_1005", "Parent lesson is invalid", HttpStatus.BAD_REQUEST),

    //====== GRAMMAR ======
    GRAMMAR_TITLE_EXISTED("GRAMMAR_1000", "Grammar title already exists", HttpStatus.CONFLICT),
    GRAMMAR_NOT_FOUND("GRAMMAR_1001", "Grammar not found", HttpStatus.NOT_FOUND),
    GRAMMAR_TITLE_INVALID("GRAMMAR_1002", "Grammar title must be between 3 and 200 characters", HttpStatus.BAD_REQUEST),
    GRAMMAR_STRUCTURE_INVALID("GRAMMAR_1003", "Grammar structure must not exceed 2000 characters", HttpStatus.BAD_REQUEST),
    GRAMMAR_EXPLANATION_INVALID("GRAMMAR_1004", "Grammar explanation must not exceed 5000 characters", HttpStatus.BAD_REQUEST),
    GRAMMAR_LEVEL_INVALID("GRAMMAR_1005", "Grammar level is required", HttpStatus.BAD_REQUEST),

    //====== VOCABULARY ======
    VOCABULARY_WORD_EXISTED("VOCABULARY_1000", "Vocabulary word already exists", HttpStatus.CONFLICT),
    VOCABULARY_NOT_FOUND("VOCABULARY_1001", "Vocabulary not found", HttpStatus.NOT_FOUND),
    VOCABULARY_WORD_INVALID("VOCABULARY_1002", "Vocabulary word must be between 1 and 100 characters", HttpStatus.BAD_REQUEST),
    VOCABULARY_TYPE_INVALID("VOCABULARY_1003", "Vocabulary type is required", HttpStatus.BAD_REQUEST),
    VOCABULARY_ROMAJI_INVALID("VOCABULARY_1004", "Vocabulary romaji must not exceed 200 characters", HttpStatus.BAD_REQUEST),
    VOCABULARY_MEANING_INVALID("VOCABULARY_1005", "Vocabulary meaning must not exceed 1000 characters", HttpStatus.BAD_REQUEST),
    VOCABULARY_LEVEL_INVALID("VOCABULARY_1006", "Vocabulary level is required", HttpStatus.BAD_REQUEST),

    //====== EXAM ======
    EXAM_TITLE_EXISTED("EXAM_1000", "Exam title already exists", HttpStatus.CONFLICT),
    EXAM_NOT_FOUND("EXAM_1001", "Exam not found", HttpStatus.NOT_FOUND),
    EXAM_TITLE_INVALID("EXAM_1002", "Exam title must be between 3 and 100 characters", HttpStatus.BAD_REQUEST),
    EXAM_DESCRIPTION_INVALID("EXAM_1003", "Exam description must not exceed 1000 characters", HttpStatus.BAD_REQUEST),
    EXAM_DURATION_INVALID("EXAM_1004", "Exam duration must be at least 1 minute", HttpStatus.BAD_REQUEST),
    EXAM_TYPE_INVALID("EXAM_1005", "Exam type is required", HttpStatus.BAD_REQUEST),
    EXAM_CODE_INVALID("EXAM_1006", "Exam code must be between 3 and 50 characters", HttpStatus.BAD_REQUEST),

    //====== QUESTION ======
    QUESTION_NOT_FOUND("QUESTION_1000", "Question not found", HttpStatus.NOT_FOUND),
    QUESTION_CONTENT_INVALID("QUESTION_1001", "Question content must be between 1 and 2000 characters", HttpStatus.BAD_REQUEST),
    QUESTION_TYPE_INVALID("QUESTION_1002", "Question type is required", HttpStatus.BAD_REQUEST),
    QUESTION_DIFFICULTY_INVALID("QUESTION_1003", "Question difficulty is required", HttpStatus.BAD_REQUEST),
    QUESTION_SCORE_INVALID("QUESTION_1004", "Question score must be within allowed range", HttpStatus.BAD_REQUEST),
    QUESTION_CATEGORY_INVALID("QUESTION_1005", "Question category is required", HttpStatus.BAD_REQUEST),

    //====== QUESTION OPTION ======
    QUESTION_OPTION_NOT_FOUND("QOPTION_1000", "Question option not found", HttpStatus.NOT_FOUND),
    QUESTION_OPTION_CONTENT_INVALID("QOPTION_1001", "Question option content must be between 1 and 2000 characters", HttpStatus.BAD_REQUEST),
    QUESTION_OPTION_ORDER_INDEX_INVALID("QOPTION_1002", "Question option order index must be >= 0", HttpStatus.BAD_REQUEST),
    QUESTION_OPTION_IS_CORRECT_INVALID("QOPTION_1003", "Question option isCorrect is required", HttpStatus.BAD_REQUEST),

    //====== COMMENT ======
    COMMENT_NOT_FOUND("COMMENT_1000", "Comment not found", HttpStatus.NOT_FOUND),
    COMMENT_CONTENT_INVALID("COMMENT_1001", "Comment content must be between 1 and 5000 characters", HttpStatus.BAD_REQUEST),
    COMMENT_TARGET_TYPE_INVALID("COMMENT_1002", "Comment target type is required", HttpStatus.BAD_REQUEST),
    COMMENT_TARGET_ID_INVALID("COMMENT_1003", "Comment target ID is required", HttpStatus.BAD_REQUEST),
    COMMENT_AUTHOR_INVALID("COMMENT_1004", "Comment author is required", HttpStatus.BAD_REQUEST),
    COMMENT_PARENT_INVALID("COMMENT_1005", "Parent comment is invalid", HttpStatus.BAD_REQUEST),

    ;

    String code;
    String message;
    HttpStatusCode httpStatusCode;
}
