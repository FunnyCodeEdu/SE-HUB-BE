package com.se.hub.modules.user.constant.permission;

public class StartDefinedPermission {
    // System-level Permissions
    public static final String SYSTEM_VIEW = "SYSTEM_VIEW";
    public static final String SYSTEM_MANAGE = "SYSTEM_MANAGE";

    public static final String ROLE_VIEW = "ROLE_VIEW";
    public static final String ROLE_MANAGE = "ROLE_MANAGE";

    public static final String PERMISSION_VIEW = "PERMISSION_VIEW";
    public static final String PERMISSION_MANAGE = "PERMISSION_MANAGE";

    public static final String AUDIT_VIEW = "AUDIT_VIEW";

    // User Permissions
    public static final String USER_VIEW = "USER_VIEW";
    public static final String USER_UPDATE = "USER_UPDATE";
    public static final String USER_DELETE = "USER_DELETE";

    public static final String USER_BLOCK = "USER_BLOCK";
    public static final String USER_UNBLOCK = "USER_UNBLOCK";

    public static final String PROFILE_VIEW = "PROFILE_VIEW";
    public static final String PROFILE_UPDATE = "PROFILE_UPDATE";

    // Learning Content Permissions (JapanLearn Hub)
    public static final String LESSON_VIEW = "LESSON_VIEW";
    public static final String LESSON_CREATE = "LESSON_CREATE";
    public static final String LESSON_UPDATE = "LESSON_UPDATE";
    public static final String LESSON_DELETE = "LESSON_DELETE";

    public static final String QUIZ_VIEW = "QUIZ_VIEW";
    public static final String QUIZ_CREATE = "QUIZ_CREATE";
    public static final String QUIZ_UPDATE = "QUIZ_UPDATE";
    public static final String QUIZ_DELETE = "QUIZ_DELETE";

    // Payment & Subscription Permissions
    public static final String PAYMENT_VIEW = "PAYMENT_VIEW";
    public static final String PAYMENT_MANAGE = "PAYMENT_MANAGE";

    public static final String SUBSCRIPTION_VIEW = "SUBSCRIPTION_VIEW";
    public static final String SUBSCRIPTION_MANAGE = "SUBSCRIPTION_MANAGE";

    private StartDefinedPermission() {
    }
}
