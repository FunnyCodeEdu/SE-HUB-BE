package com.se.hub.modules.payment.service.impl;

import com.se.hub.modules.payment.config.PaymentManagementConfig;
import com.se.hub.modules.payment.dto.request.GenerateQRRequest;
import com.se.hub.modules.payment.dto.response.QRCodeResponse;
import com.se.hub.modules.payment.service.PaymentManagementService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentManagementServiceImpl implements PaymentManagementService {

    PaymentManagementConfig config;
    RestTemplate restTemplate;

    @Override
    public QRCodeResponse generateQRCode(GenerateQRRequest request) {
        try {
            String url = config.getBaseUrl() + "/api/vietqr/generate";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> body = new HashMap<>();
            // Amount is optional - if null, don't include it or set to 0
            if (request.getAmount() != null && request.getAmount() > 0) {
                body.put("amount", request.getAmount());
            } else {
                body.put("amount", 0); // 0 means user will enter amount manually
            }
            body.put("description", request.getDescription());
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            ParameterizedTypeReference<Map<String, Object>> responseType = 
                    new ParameterizedTypeReference<Map<String, Object>>() {};
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    responseType
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                
                // Handle nested result structure from ApiResponse
                Object resultObj = responseBody.get("result");
                if (resultObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> result = (Map<String, Object>) resultObj;
                    String qrCodeUrl = (String) result.get("qrCodeUrl");
                    
                    return QRCodeResponse.builder()
                            .qrCodeUrl(qrCodeUrl)
                            .description(request.getDescription())
                            .amount(request.getAmount())
                            .build();
                } else {
                    // Direct response structure
                    String qrCodeUrl = (String) responseBody.get("qrCodeUrl");
                    
                    return QRCodeResponse.builder()
                            .qrCodeUrl(qrCodeUrl)
                            .description(request.getDescription())
                            .amount(request.getAmount())
                            .build();
                }
            }
            
            throw new RuntimeException("Failed to generate QR code from payment management service");
        } catch (Exception e) {
            log.error("Error calling payment management service to generate QR: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }
}

