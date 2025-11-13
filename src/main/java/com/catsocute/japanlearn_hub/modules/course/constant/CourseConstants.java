package com.catsocute.japanlearn_hub.modules.course.constant;

public class CourseConstants {
    //===== TABLE NAME ======
    public static final String TABLE_COURSE = "course";

    //===== COLUMN NAME ======
    public static final String COL_NAME = "name";
    public static final String COL_SPECIALIZATION = "specialization";
    public static final String COL_SEMESTER = "semester";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_SHORT_DESCRIPTION = "shortDescription";
    public static final String COL_IMG_URL = "imgUrl";

    //===== COLUMN DEFINITIONS ======
    public static final String NAME_DEFINITION = "VARCHAR(25)";
    public static final String DESCRIPTION_DEFINITION = "TEXT";
    public static final String IMG_URL_DEFINITION = "VARCHAR(255)";

    //===== VALIDATION VALUE LIMITS ======
    public static final int NAME_MIN_LENGTH = 3;
    public static final int NAME_MAX_LENGTH = 100;
    public static final int DESCRIPTION_MAX_LENGTH = 1000;
    public static final int SEMESTER_MIN = 1;
    public static final int SEMESTER_MAX = 9;


    private CourseConstants() {}
}
