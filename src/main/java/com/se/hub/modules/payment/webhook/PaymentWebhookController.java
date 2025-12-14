package com.se.hub.modules.payment.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.hub.modules.payment.config.PaymentManagementConfig;
import com.se.hub.modules.payment.service.WalletDepositService;
import com.se.hub.modules.payment.util.DepositCodeExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/webhook/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentWebhookController {
    
    ObjectMapper objectMapper;
    WalletDepositService walletDepositService;
    DepositCodeExtractor depositCodeExtractor;
    PaymentManagementConfig config;

    @PostMapping
    public ResponseEntity<String> handlePaymentWebhook(HttpServletRequest request) throws IOException {
        // Verify secure token
        String secureToken = request.getHeader("secure-token");
        if (secureToken == null || !secureToken.equals(config.getWebhookSecureToken())) {
            log.warn("Unauthorized webhook request - invalid secure token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"success\":false,\"message\":\"Unauthorized\"}");
        }
        
        // Parse request body
        JsonNode json = objectMapper.readTree(request.getInputStream());
        JsonNode transactions = json.get("data");
        
        if (transactions == null || !transactions.isArray()) {
            log.warn("Invalid webhook payload - missing data array");
            return ResponseEntity.badRequest()
                    .body("{\"success\":false,\"message\":\"Invalid payload\"}");
        }
        
        int processedCount = 0;
        int errorCount = 0;
        
        // Process each transaction
        for (JsonNode transaction : transactions) {
            try {
                JsonNode amountNode = transaction.get("amount");
                JsonNode descNode = transaction.get("description");
                
                if (amountNode == null || descNode == null) {
                    log.debug("Skipping transaction - missing amount or description");
                    continue;
                }
                
                double amount = amountNode.asDouble();
                String description = descNode.asText();
                
                // Extract deposit code
                String depositCode = depositCodeExtractor.extractDepositCode(description);
                
                if (depositCode != null) {
                    log.info("Processing SEHUB deposit transaction: depositCode={}, amount={}", 
                            depositCode, amount);
                    
                    try {
                        walletDepositService.processDepositTransaction(depositCode, amount, description);
                        processedCount++;
                        log.info("Successfully processed deposit transaction: {}", depositCode);
                    } catch (Exception e) {
                        errorCount++;
                        log.error("Failed to process deposit transaction: depositCode={}, error={}", 
                                depositCode, e.getMessage(), e);
                    }
                } else {
                    log.debug("Transaction does not contain SEHUB deposit code: {}", description);
                }
            } catch (Exception e) {
                errorCount++;
                log.error("Error processing transaction: {}", e.getMessage(), e);
            }
        }
        
        String responseMessage = String.format(
                "{\"success\":true,\"processed\":%d,\"errors\":%d}", 
                processedCount, errorCount);
        
        return ResponseEntity.ok(responseMessage);
    }
}

