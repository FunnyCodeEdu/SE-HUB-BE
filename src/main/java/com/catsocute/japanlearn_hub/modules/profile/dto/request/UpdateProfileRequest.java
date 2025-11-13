package com.catsocute.japanlearn_hub.modules.profile.dto.request;

import com.catsocute.japanlearn_hub.modules.profile.constant.profile.ProfileConstants;
import com.catsocute.japanlearn_hub.modules.profile.constant.profile.ProfileErrorCodeConstants;
import com.catsocute.japanlearn_hub.modules.profile.enums.GenderEnums;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProfileRequest {
    @NotBlank(message = ProfileErrorCodeConstants.FULL_NAME_NOT_BLANK)
    @Size(min = ProfileConstants.FULL_NAME_MIN,
            max = ProfileConstants.FULL_NAME_MAX,
            message = ProfileErrorCodeConstants.FULL_NAME_SIZE_INVALID)
    String fullName;

    @Pattern(regexp = ProfileConstants.PHONE_NUMBER_PATTERN,
            message = ProfileErrorCodeConstants.PHONE_NUMBER_PATTERN_INVALID)
    @Size(min = ProfileConstants.PHONE_NUM_MIN_LENGTH,
            max = ProfileConstants.PHONE_NUM_MAX_LENGTH,
            message = ProfileErrorCodeConstants.PHONE_NUMBER_PATTERN_INVALID)
    String phoneNum;

    @Pattern(regexp = ProfileConstants.EMAIL_PATTERN,
            message = ProfileErrorCodeConstants.EMAIL_INVALID_FORMAT)
    @Size(max = ProfileConstants.EMAIL_MAX_LENGTH,
            message = ProfileErrorCodeConstants.EMAIL_SIZE_INVALID)
    String email;

    @Enumerated(EnumType.STRING)
    @NotNull(message = ProfileErrorCodeConstants.GENDER_NOT_NULL)
    GenderEnums gender;

    boolean verified;
    boolean blocked;
    boolean active;
}
