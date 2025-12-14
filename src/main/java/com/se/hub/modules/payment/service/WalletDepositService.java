package com.se.hub.modules.payment.service;

import com.se.hub.modules.payment.dto.request.DepositRequest;
import com.se.hub.modules.payment.dto.response.DepositResponse;

public interface WalletDepositService {
    DepositResponse createDeposit(DepositRequest request);
    void processDepositTransaction(String depositCode, Double amount, String description);
}

