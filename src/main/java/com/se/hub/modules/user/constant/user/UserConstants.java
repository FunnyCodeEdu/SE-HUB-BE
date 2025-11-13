package com.se.hub.modules.user.constant.user;

public class UserConstants {
    //======= User table name ========
    public static final String TABLE_USER = "users";

    //======= User column name ========
    public static final String COL_USERID = "userId";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_STATUS = "status";

    //======= User column definition ========
    public static final String USERNAME_DEFINITION = "VARCHAR(100)";
    public static final String PASSWORD_DEFINITION = "VARCHAR(100)";

    //======= User validation values limit ========
    public static final int MIN_CHARS_USERNAME = 5;
    public static final int MAX_CHARS_USERNAME = 50;
    public static final int MIN_CHARS_PASSWORD = 8;
    public static final int MAX_CHARS_PASSWORD = 100;
}
