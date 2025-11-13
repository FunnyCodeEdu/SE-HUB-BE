package com.catsocute.japanlearn_hub.modules.lesson.constant.lesson;

public class LessonConstants {
    //===== TABLE NAME ======
    public static final String TABLE_LESSON = "lesson";

    //===== COLUMN NAME ======
    public static final String COL_TITLE = "title";
    public static final String COL_TYPE = "type";
    public static final String COL_PARENT_LESSON = "parentLesson";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_COURSE_ID = "courseId";

    //===== COLUMN DEFINITIONS ======
    public static final String TITLE_DEFINITION = "VARCHAR(100)";
    public static final String TYPE_DEFINITION = "VARCHAR(30)";
    public static final String DESCRIPTION_DEFINITION = "TEXT";

    //===== VALIDATION VALUE LIMITS ======
    public static final int TITLE_MIN_LENGTH = 3;
    public static final int TITLE_MAX_LENGTH = 100;
    public static final int DESCRIPTION_MAX_LENGTH = 1000;

    private LessonConstants() {}
}
