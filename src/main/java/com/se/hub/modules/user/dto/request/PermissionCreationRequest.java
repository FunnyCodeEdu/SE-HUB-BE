package com.se.hub.modules.user.dto.request;

import com.se.hub.modules.user.constant.permission.PermissionConstants;
import com.se.hub.modules.user.constant.permission.PermissionErrorCodeConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionCreationRequest {
    @NotBlank(message = PermissionErrorCodeConstants.PERMISSION_NAME_NOT_BLANK)
    @Size(min = PermissionConstants.MIN_CHARS_PERMISSION_NAME,
            max = PermissionConstants.MAX_CHARS_PERMISSION_NAME,
            message = PermissionErrorCodeConstants.PERMISSION_NAME_INVALID)
    String name;

    @Size(min = PermissionConstants.MIN_CHARS_DESCRIPTION,
            max = PermissionConstants.MAX_CHARS_DESCRIPTION,
            message = PermissionErrorCodeConstants.PERMISSION_DESCRIPTION_INVALID)
    String description;
}

