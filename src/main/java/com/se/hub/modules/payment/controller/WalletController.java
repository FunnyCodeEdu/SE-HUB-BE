package com.se.hub.modules.payment.controller;

import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.modules.payment.constant.wallet.WalletControllerConstants;
import com.se.hub.modules.payment.service.WalletService;
import com.se.hub.common.controller.BaseController;
import com.se.hub.common.dto.response.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = WalletControllerConstants.TAG_NAME, description = WalletControllerConstants.TAG_DESCRIPTION)
@RequestMapping("/wallet")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WalletController extends BaseController {
    
    WalletService walletService;

    @PostMapping("/create-for-existing-profiles")
    @Operation(
        summary = WalletControllerConstants.CREATE_FOR_EXISTING_OPERATION_SUMMARY,
        description = WalletControllerConstants.CREATE_FOR_EXISTING_OPERATION_DESCRIPTION
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = WalletControllerConstants.CREATE_FOR_EXISTING_SUCCESS_RESPONSE),
        @ApiResponse(responseCode = "500", description = WalletControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<CreateWalletsResponse>> createWalletsForExistingProfiles() {
        log.info("Creating wallets for existing profiles without wallets");
        int createdCount = walletService.createWalletsForExistingProfiles();
        log.info("Successfully created {} wallets for existing profiles", createdCount);
        
        CreateWalletsResponse response = new CreateWalletsResponse(createdCount);
        return success(response, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }

    public static class CreateWalletsResponse {
        private final int createdCount;
        private final String message;

        public CreateWalletsResponse(int createdCount) {
            this.createdCount = createdCount;
            this.message = String.format("Created %d wallets for existing profiles", createdCount);
        }

        public int getCreatedCount() {
            return createdCount;
        }

        public String getMessage() {
            return message;
        }
    }
}

