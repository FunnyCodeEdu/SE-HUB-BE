package com.catsocute.japanlearn_hub.modules.exam.constant.question;

public class QuestionConstants {
    //===== TABLE NAME ======
    public static final String TABLE_QUESTION = "question";

    //===== COLUMN NAME ======
    public static final String COL_EXAM_ID = "examId";
    public static final String COL_CONTENT = "content";
    public static final String COL_QUESTION_TYPE = "questionType";
    public static final String COL_DIFFICULTY = "difficulty";
    public static final String COL_SCORE = "score";
    public static final String COL_CATEGORY = "category";
    public static final String COL_JLPT_LEVEL = "jlptLevel";

    //===== COLUMN DEFINITIONS ======
    public static final String CONTENT_DEFINITION = "TEXT";
    public static final String QUESTION_TYPE_DEFINITION = "VARCHAR(30)";
    public static final String DIFFICULTY_DEFINITION = "VARCHAR(30)";
    public static final String CATEGORY_DEFINITION = "VARCHAR(30)";
    public static final String JLPT_LEVEL_DEFINITION = "VARCHAR(10)";

    //===== VALIDATION LIMITS ======
    public static final int CONTENT_MIN_LENGTH = 1;
    public static final int CONTENT_MAX_LENGTH = 2000;
    public static final int SCORE_MIN = 0;
    public static final int SCORE_MAX = 1000;

    private QuestionConstants() {}
}


