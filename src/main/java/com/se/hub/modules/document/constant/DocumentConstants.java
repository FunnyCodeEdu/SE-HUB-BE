package com.se.hub.modules.document.constant;

public class DocumentConstants {
    public static final String TABLE_DOCUMENT = "documents";

    public static final String COL_COURSE_ID = "course_id";
    public static final String COL_DOCUMENT_NAME = "document_name";
    public static final String COL_DESCRIPT = "descript";
    public static final String COL_SEMESTER = "semester";
    public static final String COL_MAJOR = "major";
    public static final String COL_UPLOADED_BY = "uploaded_by";
    public static final String COL_FILE_PATH = "file_path";
    public static final String COL_FILE_TYPE = "file_type";
    public static final String COL_FILE_SIZE = "file_size";
    public static final String COL_IS_APPROVED = "is_approved";

    public static final int DOCUMENT_NAME_MAX_LENGTH = 200;
    public static final int DESCRIPT_MAX_LENGTH = 1000;
    public static final int SEMESTER_MAX_LENGTH = 50;
    public static final int MAJOR_MAX_LENGTH = 100;
    public static final int FILE_PATH_MAX_LENGTH = 500;
    public static final int FILE_TYPE_MAX_LENGTH = 20;

    public static final String DOCUMENT_NAME_DEFINITION = "VARCHAR(200)";
    public static final String DESCRIPT_DEFINITION = "TEXT";
    public static final String SEMESTER_DEFINITION = "VARCHAR(50)";
    public static final String MAJOR_DEFINITION = "VARCHAR(100)";
    public static final String FILE_PATH_DEFINITION = "VARCHAR(500)";
    public static final String FILE_TYPE_DEFINITION = "VARCHAR(20)";

    public static final long DOCUMENT_MAX_FILE_SIZE_BYTES = 20L * 1024 * 1024;
    public static final int DOCUMENT_MAX_FILE_SIZE_MB = 20;

    public static final long IMAGE_MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024;
    public static final int IMAGE_MAX_FILE_SIZE_MB = 5;

    private DocumentConstants() {}
}

