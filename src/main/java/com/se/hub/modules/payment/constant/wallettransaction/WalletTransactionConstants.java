package com.se.hub.modules.payment.constant.wallettransaction;

public final class WalletTransactionConstants {
    private WalletTransactionConstants() {}

    // ===== TABLE NAME =====
    public static final String TABLE_WALLET_TRANSACTION = "wallet_transaction";

    // ===== COLUMN NAMES =====
    public static final String AMOUNT = "amount";
    public static final String DIRECTION = "direction";
    public static final String SOURCE = "source";
    public static final String STATUS = "status";
    public static final String CREATED_AT = "createdAt";
    public static final String WALLET_ID = "walletId";

    // ===== COLUMN DEFINITIONS =====
    public static final String AMOUNT_DEFINITION = "DECIMAL(19,2)";
    public static final String DIRECTION_DEFINITION = "VARCHAR(10)";
    public static final String SOURCE_DEFINITION = "VARCHAR(50)";
    public static final String STATUS_DEFINITION = "VARCHAR(20)";
}

