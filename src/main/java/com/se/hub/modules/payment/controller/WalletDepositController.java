package com.se.hub.modules.payment.controller;

import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.controller.BaseController;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.modules.payment.constant.wallet.WalletControllerConstants;
import com.se.hub.modules.payment.dto.request.DepositRequest;
import com.se.hub.modules.payment.dto.response.DepositResponse;
import com.se.hub.modules.payment.service.WalletDepositService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = WalletControllerConstants.TAG_NAME, description = WalletControllerConstants.TAG_DESCRIPTION)
@RequestMapping("/wallet/deposit")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WalletDepositController extends BaseController {
    
    WalletDepositService walletDepositService;

    @PostMapping
    @Operation(
        summary = "Create deposit QR code",
        description = "Generate QR code for wallet deposit. The deposit code will be in format SEHUB<depositCode>. " +
                      "The QR code does not include amount - user will enter the amount manually when transferring."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "QR code generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Wallet not found"),
        @ApiResponse(responseCode = "500", description = WalletControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<DepositResponse>> createDeposit(@Valid @RequestBody DepositRequest request) {
        log.info("Creating deposit QR code (user will enter amount manually)");
        DepositResponse response = walletDepositService.createDeposit(request);
        log.info("Deposit QR code created successfully");
        return success(response, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }
}

