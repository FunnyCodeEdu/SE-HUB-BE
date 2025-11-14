package com.se.hub.modules.user.dto.request;

import com.se.hub.modules.user.constant.role.RoleConstants;
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
public class RoleUpdateRequest {
    @Size(min = RoleConstants.MAX_CHARS_DESCRIPTION,
            max =  RoleConstants.MAX_CHARS_DESCRIPTION)
    String description;
}
