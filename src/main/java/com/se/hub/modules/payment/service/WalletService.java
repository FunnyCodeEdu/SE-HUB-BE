package com.se.hub.modules.payment.service;

import com.se.hub.modules.payment.entity.Wallet;
import com.se.hub.modules.profile.entity.Profile;

public interface WalletService {
    Wallet createDefault(Profile profile);
    Wallet ensureWallet(Profile profile);
    int createWalletsForExistingProfiles();
}

