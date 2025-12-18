package com.se.hub.modules.interaction.constant;

public class ReportConstants {
    //===== TABLE NAME ======
    public static final String TABLE_REPORT = "report";
    public static final String TABLE_REPORT_REASON = "report_reason";

    //===== COLUMN NAME ======
    public static final String COL_REPORTER_ID = "reporterId";
    public static final String COL_TARGET_TYPE = "targetType";
    public static final String COL_TARGET_ID = "targetId";
    public static final String COL_STATUS = "status";
    public static final String COL_REPORT_ID = "reportId";
    public static final String COL_REPORT_TYPE = "reportType";
    public static final String COL_DESCRIPTION = "description";

    //===== COLUMN DEFINITIONS ======
    public static final String TARGET_TYPE_DEFINITION = "VARCHAR(20)";
    public static final String TARGET_ID_DEFINITION = "VARCHAR(36)";
    public static final String STATUS_DEFINITION = "VARCHAR(20)";
    public static final String REPORT_TYPE_DEFINITION = "VARCHAR(20)";
    public static final String DESCRIPTION_DEFINITION = "TEXT";

    //===== VALIDATION VALUE LIMITS ======
    public static final int TARGET_ID_MAX_LENGTH = 36;
    public static final int DESCRIPTION_MAX_LENGTH = 1000;


    private ReportConstants() {}
}

