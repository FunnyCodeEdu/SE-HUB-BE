package com.se.hub.modules.payment.constant.wallet;

public final class WalletConstants {
    private WalletConstants() {}

    // ===== TABLE NAME =====
    public static final String TABLE_WALLET = "wallet";

    // ===== COLUMN NAMES =====
    public static final String PROFILE_ID = "profileId";
    public static final String SE_TOKEN = "seToken";
    public static final String STATUS = "status";
    public static final String DEPOSIT_CODE = "depositCode";

    // ===== COLUMN DEFINITIONS =====
    public static final String SE_TOKEN_DEFINITION = "VARCHAR(255)";
    public static final String STATUS_DEFINITION = "VARCHAR(20)";
    public static final String DEPOSIT_CODE_DEFINITION = "VARCHAR(50)";
}

