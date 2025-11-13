package com.catsocute.japanlearn_hub.modules.profile.constant.profile;

public class ProfileConstants {
    //===== TABLE NAME ======
    public static final String TABLE_PROFILE = "profile";
    public static final String PROFILE_ID = "profileId";

    //===== COLUMN NAME ======
    public static final String FULL_NAME = "fullName";
    public static final String PHONE_NUM = "phoneNum";
    public static final String EMAIL = "email";
    public static final String AVATAR_URL = "avtUrl";
    public static final String GENDER = "gender";
    public static final String IS_VERIFIED = "isVerified";
    public static final String IS_BLOCKED = "isBlocked";
    public static final String IS_ACTIVE = "isActive";
    public static final String USER_LEVEL_ID = "userLevelId";

    //===== COLUMN DEFINITIONS ======
    public static final String FULL_NAME_DEFINITION = "VARCHAR(50)";
    public static final String PHONE_NUM_DEFINITION = "VARCHAR(15)";
    public static final String EMAIL_DEFINITION = "VARCHAR(100)";
    public static final String AVATAR_URL_DEFINITION = "VARCHAR(255)";
    public static final String GENDER_DEFINITION = "VARCHAR(10)";

    //===== VALIDATION VALUE LIMITS ======
    public static final int FULL_NAME_MIN = 1;
    public static final int FULL_NAME_MAX = 50;
    public static final int EMAIL_MAX_LENGTH = 100;
    public static final int URL_MAX_LENGTH = 255;
    public static final int PHONE_NUM_MAX_LENGTH = 11;
    public static final int PHONE_NUM_MIN_LENGTH = 10;

    //===== PATTERNS ======
    public static final String PHONE_NUMBER_PATTERN = "^[0-9]{10,11}$";
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String DEFAULT_AVT_URL = "Avatar url";

    private ProfileConstants() {}
}
