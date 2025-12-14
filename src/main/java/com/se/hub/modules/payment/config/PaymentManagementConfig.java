package com.se.hub.modules.payment.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class PaymentManagementConfig {
    
    @Value("${paymentmanagement.api.base-url}")
    private String baseUrl;
    
    @Value("${paymentmanagement.webhook.secure-token}")
    private String webhookSecureToken;
}

