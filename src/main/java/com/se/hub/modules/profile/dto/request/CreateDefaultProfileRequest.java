package com.se.hub.modules.profile.dto.request;

import com.se.hub.modules.profile.constant.profile.ProfileErrorCodeConstants;
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
public class CreateDefaultProfileRequest {
    @NotBlank(message = ProfileErrorCodeConstants.USER_ID_NOT_BLANK)
    String userId;
}
