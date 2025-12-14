package com.se.hub.modules.payment.constant.wallet;

public final class WalletControllerConstants {
    private WalletControllerConstants() {}

    // ===== TAG =====
    public static final String TAG_NAME = "Wallet";
    public static final String TAG_DESCRIPTION = "Wallet management APIs";

    // ===== OPERATION SUMMARIES =====
    public static final String CREATE_FOR_EXISTING_OPERATION_SUMMARY = "Create wallets for existing profiles";
    public static final String CREATE_FOR_EXISTING_OPERATION_DESCRIPTION = 
            "Create wallets for all existing profiles that don't have a wallet yet. " +
            "This is a one-time migration endpoint to create wallets for existing users.";

    // ===== RESPONSES =====
    public static final String CREATE_FOR_EXISTING_SUCCESS_RESPONSE = "Wallets created successfully for existing profiles";
    public static final String INTERNAL_ERROR_RESPONSE = "Internal server error";
}

