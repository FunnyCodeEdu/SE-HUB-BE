package com.se.hub.modules.payment.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.payment.constant.wallet.WalletConstants;
import com.se.hub.modules.payment.enums.WalletStatus;
import com.se.hub.modules.profile.entity.Profile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = WalletConstants.TABLE_WALLET)
public class Wallet extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = WalletConstants.PROFILE_ID,
            referencedColumnName = BaseFieldConstant.ID)
    @MapsId
    Profile profile;

    @Column(name = WalletConstants.SE_TOKEN,
            columnDefinition = WalletConstants.SE_TOKEN_DEFINITION)
    String seToken;

    @Enumerated(EnumType.STRING)
    @Column(name = WalletConstants.STATUS,
            columnDefinition = WalletConstants.STATUS_DEFINITION)
    WalletStatus status;

    @Column(name = WalletConstants.DEPOSIT_CODE,
            unique = true,
            columnDefinition = WalletConstants.DEPOSIT_CODE_DEFINITION)
    String depositCode;

    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY)
    List<WalletTransaction> walletTransactions;
}
