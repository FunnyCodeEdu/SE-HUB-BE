package com.se.hub.modules.exam.constant.exam;

public class ExamConstants {
    //===== TABLE NAME ======
    public static final String TABLE_EXAM = "exam";

    //===== COLUMN NAME ======
    public static final String COL_COURSE_ID = "courseId";
    public static final String COL_TITLE = "title";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_DURATION_MINUTES = "durationMinutes";
    public static final String COL_EXAM_TYPE = "examType";
    public static final String COL_EXAM_CODE = "examCode";

    //===== COLUMN DEFINITIONS ======
    public static final String TITLE_DEFINITION = "VARCHAR(100)";
    public static final String DESCRIPTION_DEFINITION = "TEXT";
    public static final String EXAM_TYPE_DEFINITION = "VARCHAR(30)";
    public static final String EXAM_CODE_DEFINITION = "VARCHAR(50)";

    //===== VALIDATION VALUE LIMITS ======
    public static final int TITLE_MIN_LENGTH = 3;
    public static final int TITLE_MAX_LENGTH = 100;
    public static final int DESCRIPTION_MAX_LENGTH = 1000;
    public static final int EXAM_CODE_MIN_LENGTH = 3;
    public static final int EXAM_CODE_MAX_LENGTH = 50;
    public static final int DURATION_MIN = 1;

    private ExamConstants() {}
}


