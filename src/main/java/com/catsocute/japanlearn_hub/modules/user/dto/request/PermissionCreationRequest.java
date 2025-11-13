package com.catsocute.japanlearn_hub.modules.user.dto.request;

import com.catsocute.japanlearn_hub.modules.user.constant.permission.PermissionConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank
    @NotNull
    @Size(min = PermissionConstants.MIN_CHARS_PERMISSION_NAME,
            max = PermissionConstants.MAX_CHARS_PERMISSION_NAME)
    String name;

    @Size(min = PermissionConstants.MIN_CHARS_DESCRIPTION,
            max = PermissionConstants.MAX_CHARS_DESCRIPTION)
    String description;

    public String getName() {
        return this.name.toUpperCase();
    }
}
