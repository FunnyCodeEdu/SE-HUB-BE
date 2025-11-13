package com.catsocute.japanlearn_hub.modules.lesson.constant.vocabulary;

public class VocabularyConstants {
    //===== TABLE NAME ======
    public static final String TABLE_VOCABULARY = "vocabulary";

    //===== COLUMN NAME ======
    public static final String COL_WORD = "word";
    public static final String COL_TYPE = "type";
    public static final String COL_ROMAJI = "romaji";
    public static final String COL_MEANING = "meaning";
    public static final String COL_LEVEL = "level";

    //===== COLUMN DEFINITIONS ======
    public static final String WORD_DEFINITION = "VARCHAR(100)";
    public static final String ROMAJI_DEFINITION = "VARCHAR(200)";
    public static final String MEANING_DEFINITION = "TEXT";

    //===== VALIDATION VALUE LIMITS ======
    public static final int WORD_MIN_LENGTH = 1;
    public static final int WORD_MAX_LENGTH = 100;
    public static final int ROMAJI_MAX_LENGTH = 200;
    public static final int MEANING_MAX_LENGTH = 1000;

    private VocabularyConstants() {}
}

