package com.catsocute.japanlearn_hub.modules.exam.constant.question_option;

public class QuestionOptionConstants {
    //===== TABLE NAME ======
    public static final String TABLE_QUESTION_OPTION = "question_option";

    //===== COLUMN NAME ======
    public static final String COL_QUESTION_ID = "questionId";
    public static final String COL_CONTENT = "content";
    public static final String COL_ORDER_INDEX = "orderIndex";
    public static final String COL_IS_CORRECT = "isCorrect";

    //===== COLUMN DEFINITIONS ======
    public static final String CONTENT_DEFINITION = "TEXT";

    //===== VALIDATION LIMITS ======
    public static final int CONTENT_MIN_LENGTH = 1;
    public static final int CONTENT_MAX_LENGTH = 2000;
    public static final int ORDER_INDEX_MIN = 0;

    private QuestionOptionConstants() {}
}


