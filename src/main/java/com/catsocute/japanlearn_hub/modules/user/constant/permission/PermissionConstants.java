package com.catsocute.japanlearn_hub.modules.user.constant.permission;

public class PermissionConstants {
    //======= Permission table name ========
    public static final String TABLE_PERMISSION = "permission";

    //======= Permission column name ========
    public static final String COL_PERMISSION_NAME = "permissionName";
    public static final String  COL_DESCRIPTION = "description";

    //======= Permission column definition ========
    public static final String NAME_DEFINITION = "VARCHAR(25)";
    public static final String DESCRIPTION_DEFINITION = "VARCHAR(255)";

    //======= Permission validation values limit ========
    public static final int MIN_CHARS_PERMISSION_NAME = 1;
    public static final int MAX_CHARS_PERMISSION_NAME = 25;
    public static final int MIN_CHARS_DESCRIPTION = 1;
    public static final int MAX_CHARS_DESCRIPTION = 255;
}
