package com.se.hub.modules.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {
    
    @NotBlank(message = "Role name is required")
    private String roleName;
}

