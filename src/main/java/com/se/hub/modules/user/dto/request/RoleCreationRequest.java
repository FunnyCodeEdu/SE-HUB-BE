package com.se.hub.modules.user.dto.request;

import com.se.hub.modules.user.constant.role.RoleConstants;
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
public class RoleCreationRequest {
    @NotNull
    @NotBlank
    @Size(min = RoleConstants.MIN_CHARS_ROLE_NAME,
            max = RoleConstants.MAX_CHARS_ROLE_NAME)
    String name;

    @Size(min = RoleConstants.MAX_CHARS_DESCRIPTION,
            max =  RoleConstants.MAX_CHARS_DESCRIPTION)
    String description;

    public String getName() {
        return this.name.toUpperCase();
    }
}
