package com.se.hub.modules.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenerateQRRequest {
    
    // Amount is optional - if null or 0, QR will not have amount, user will enter manually
    Double amount;
    
    @NotBlank(message = "Description is required")
    String description;
}

