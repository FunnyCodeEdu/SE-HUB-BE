package com.se.hub.modules.exam.constant.answer_report;

public class AnswerReportConstants {
    //===== TABLE NAME ======
    public static final String TABLE_ANSWER_REPORT = "answer_report";

    //===== COLUMN NAME ======
    public static final String COL_QUESTION_ID = "questionId";
    public static final String COL_QUESTION_OPTION_ID = "questionOptionId";
    public static final String COL_REPORTER_ID = "reporterId";
    public static final String COL_SUGGESTED_CORRECT_ANSWER = "suggestedCorrectAnswer";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_STATUS = "status";
    public static final String COL_ADMIN_ID = "adminId";

    //===== COLUMN DEFINITIONS ======
    public static final String STATUS_DEFINITION = "VARCHAR(20)";
    public static final String DESCRIPTION_DEFINITION = "TEXT";
    public static final String SUGGESTED_CORRECT_ANSWER_DEFINITION = "TEXT";

    //===== VALIDATION LIMITS ======
    public static final int DESCRIPTION_MAX_LENGTH = 1000;
    public static final int SUGGESTED_CORRECT_ANSWER_MAX_LENGTH = 2000;

    private AnswerReportConstants() {}
}

