package com.se.hub.modules.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    
    @NotBlank(message = "Status is required")
    private String status; // ACTIVE or INACTIVE
}

