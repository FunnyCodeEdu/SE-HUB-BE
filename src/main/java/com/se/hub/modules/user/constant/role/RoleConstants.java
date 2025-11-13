package com.se.hub.modules.user.constant.role;

public class RoleConstants {
    //======= Role table name ========
    public static final String TABLE_ROLE = "role";

    //======= Role column name ========
    public static final String COL_ROLE_NAME = "roleName";
    public static final String  COL_DESCRIPTION = "description";

    //======= Role column definition ========
    public static final String NAME_DEFINITION = "VARCHAR(25)";
    public static final String DESCRIPTION_DEFINITION = "VARCHAR(255)";

    //======= Role validation values limit ========
    public static final int MIN_CHARS_ROLE_NAME = 1;
    public static final int MAX_CHARS_ROLE_NAME = 25;
    public static final int MIN_CHARS_DESCRIPTION = 1;
    public static final int MAX_CHARS_DESCRIPTION = 255;
}
