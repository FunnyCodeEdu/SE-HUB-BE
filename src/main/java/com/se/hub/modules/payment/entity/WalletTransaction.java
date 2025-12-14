package com.se.hub.modules.payment.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.payment.constant.wallettransaction.WalletTransactionConstants;
import com.se.hub.modules.payment.enums.TransactionDirection;
import com.se.hub.modules.payment.enums.TransactionSource;
import com.se.hub.modules.payment.enums.TransactionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = WalletTransactionConstants.TABLE_WALLET_TRANSACTION)
public class WalletTransaction extends BaseEntity {

    @Column(name = WalletTransactionConstants.AMOUNT,
            nullable = false,
            columnDefinition = WalletTransactionConstants.AMOUNT_DEFINITION)
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = WalletTransactionConstants.DIRECTION,
            nullable = false,
            columnDefinition = WalletTransactionConstants.DIRECTION_DEFINITION)
    TransactionDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(name = WalletTransactionConstants.SOURCE,
            nullable = false,
            columnDefinition = WalletTransactionConstants.SOURCE_DEFINITION)
    TransactionSource source;

    @Enumerated(EnumType.STRING)
    @Column(name = WalletTransactionConstants.STATUS,
            nullable = false,
            columnDefinition = WalletTransactionConstants.STATUS_DEFINITION)
    TransactionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = WalletTransactionConstants.WALLET_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Wallet wallet;
}
