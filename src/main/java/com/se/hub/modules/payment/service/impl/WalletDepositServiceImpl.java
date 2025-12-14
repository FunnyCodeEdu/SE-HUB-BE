package com.se.hub.modules.payment.service.impl;

import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.payment.dto.request.DepositRequest;
import com.se.hub.modules.payment.dto.request.GenerateQRRequest;
import com.se.hub.modules.payment.dto.response.DepositResponse;
import com.se.hub.modules.payment.dto.response.QRCodeResponse;
import com.se.hub.modules.payment.entity.Wallet;
import com.se.hub.modules.payment.entity.WalletTransaction;
import com.se.hub.modules.payment.enums.TransactionDirection;
import com.se.hub.modules.payment.enums.TransactionSource;
import com.se.hub.modules.payment.enums.TransactionStatus;
import com.se.hub.modules.payment.repository.WalletRepository;
import com.se.hub.modules.payment.repository.WalletTransactionRepository;
import com.se.hub.modules.payment.service.PaymentManagementService;
import com.se.hub.modules.payment.service.WalletDepositService;
import com.se.hub.modules.profile.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WalletDepositServiceImpl implements WalletDepositService {

    WalletRepository walletRepository;
    WalletTransactionRepository walletTransactionRepository;
    ProfileRepository profileRepository;
    PaymentManagementService paymentManagementService;

    @Override
    @Transactional
    public DepositResponse createDeposit(DepositRequest request) {
        String currentUserId = AuthUtils.getCurrentUserId();
        
        // Get profile and wallet
        var profile = profileRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        
        Wallet wallet = walletRepository.findByProfile_Id(profile.getId())
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        
        // Generate deposit code format: SEHUB<depositCode>
        String depositCode = wallet.getDepositCode();
        if (depositCode == null || depositCode.isEmpty()) {
            throw new AppException(ErrorCode.DATA_NOT_FOUND);
        }
        
        String fullDescription = "SEHUB" + depositCode;
        
        // Generate QR code from payment management service (without amount - user will enter manually)
        GenerateQRRequest qrRequest = GenerateQRRequest.builder()
                .amount(null) // No amount - user will enter when transferring
                .description(fullDescription)
                .build();
        
        QRCodeResponse qrResponse = paymentManagementService.generateQRCode(qrRequest);
        
        return DepositResponse.builder()
                .depositCode(fullDescription)
                .qrCodeUrl(qrResponse.getQrCodeUrl())
                .amount(null) // No amount - user will enter when transferring
                .description(fullDescription)
                .build();
    }

    @Override
    @Transactional
    public void processDepositTransaction(String depositCode, Double amount, String description) {
        log.info("Processing deposit transaction: depositCode={}, amount={}, description={}", 
                depositCode, amount, description);
        
        // Extract deposit code (remove SEHUB prefix if present)
        final String code;
        if (depositCode.startsWith("SEHUB")) {
            code = depositCode.substring(5); // Remove "SEHUB" prefix
        } else {
            code = depositCode;
        }
        
        // Find wallet by deposit code
        Wallet wallet = walletRepository.findByDepositCode(code)
                .orElseThrow(() -> {
                    log.error("Wallet not found for deposit code: {}", code);
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                });
        
        // Check if transaction already exists (prevent duplicate processing)
        // You might want to add a unique constraint or check based on amount + timestamp
        
        // Create transaction
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .amount(BigDecimal.valueOf(amount))
                .direction(TransactionDirection.IN)
                .source(TransactionSource.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .build();
        
        walletTransactionRepository.save(transaction);
        
        log.info("Successfully processed deposit transaction for wallet: {}, amount: {}", 
                wallet.getId(), amount);
    }
}

