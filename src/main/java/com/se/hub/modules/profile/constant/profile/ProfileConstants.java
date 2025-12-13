package com.se.hub.modules.profile.constant.profile;

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
    public static final String BIO = "bio";
    public static final String ADDRESS = "address";
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    public static final String MAJOR = "major";
    public static final String USERNAME = "username";
    public static final String USER_ID = "userId";

    //===== COLUMN DEFINITIONS ======
    public static final String FULL_NAME_DEFINITION = "VARCHAR(50)";
    public static final String PHONE_NUM_DEFINITION = "VARCHAR(15)";
    public static final String EMAIL_DEFINITION = "VARCHAR(100)";
    public static final String AVATAR_URL_DEFINITION = "VARCHAR(255)";
    public static final String GENDER_DEFINITION = "VARCHAR(10)";

    //===== VALIDATION VALUE LIMITS ======
    public static final int FULL_NAME_MIN = 2;
    public static final int FULL_NAME_MAX = 50;
    public static final int EMAIL_MAX_LENGTH = 255;
    public static final int URL_MAX_LENGTH = 500;
    public static final int PHONE_NUM_MAX_LENGTH = 15;
    public static final int PHONE_NUM_MIN_LENGTH = 10;
    public static final int BIO_MAX = 500;
    public static final int ADDRESS_MAX = 255;
    public static final int MAJOR_MAX = 100;
    public static final int USERNAME_MAX = 50;

    //===== PATTERNS ======
    public static final String PHONE_NUMBER_PATTERN = "^[0-9]{10,15}$";
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String URL_PATTERN = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$";
    public static final String FULL_NAME_PATTERN = "^[a-zA-ZÀ-ỹ\\s]{2,50}$";
    public static final String DEFAULT_AVT_URL = "Avatar url";

    private ProfileConstants() {}
}
