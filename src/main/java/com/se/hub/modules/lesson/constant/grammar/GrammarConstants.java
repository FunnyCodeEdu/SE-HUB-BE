package com.se.hub.modules.lesson.constant.grammar;

public class GrammarConstants {
    //===== TABLE NAME ======
    public static final String TABLE_GRAMMAR = "grammar";

    //===== COLUMN NAME ======
    public static final String COL_TITLE = "title";
    public static final String COL_STRUCTURE = "structure";
    public static final String COL_EXPLANATION = "explanation";
    public static final String COL_LEVEL = "level";

    //===== COLUMN DEFINITIONS ======
    public static final String TITLE_DEFINITION = "VARCHAR(200)";
    public static final String STRUCTURE_DEFINITION = "TEXT";
    public static final String EXPLANATION_DEFINITION = "TEXT";

    //===== VALIDATION VALUE LIMITS ======
    public static final int TITLE_MIN_LENGTH = 3;
    public static final int TITLE_MAX_LENGTH = 200;
    public static final int STRUCTURE_MAX_LENGTH = 2000;
    public static final int EXPLANATION_MAX_LENGTH = 5000;

    private GrammarConstants() {}
}

