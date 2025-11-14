package com.se.hub.modules.profile.dto.request;

import com.se.hub.modules.profile.constant.profile.ProfileConstants;
import com.se.hub.modules.profile.constant.profile.ProfileErrorCodeConstants;
import com.se.hub.modules.profile.enums.GenderEnums;
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
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.se.hub.modules.profile.util.SocialLinksDeserializer;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request DTO for updating profile information")
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

    @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
    @Size(max = ProfileConstants.URL_MAX_LENGTH,
            message = ProfileErrorCodeConstants.AVATAR_URL_SIZE_INVALID)
    String avtUrl;

    @Schema(description = "Biography", example = "Software developer passionate about technology")
    @Size(max = ProfileConstants.BIO_MAX,
            message = ProfileErrorCodeConstants.BIO_SIZE_INVALID)
    String bio;

    @Schema(description = "Address", example = "123 Main St, City, Country")
    @Size(max = ProfileConstants.ADDRESS_MAX,
            message = ProfileErrorCodeConstants.ADDRESS_SIZE_INVALID)
    String address;

    @Schema(description = "Website URL", example = "https://example.com")
    @Size(max = ProfileConstants.URL_MAX_LENGTH,
            message = ProfileErrorCodeConstants.WEBSITE_SIZE_INVALID)
    @Pattern(regexp = ProfileConstants.URL_PATTERN,
            message = ProfileErrorCodeConstants.WEBSITE_PATTERN_INVALID)
    String website;

    @Schema(description = "Social links as JSON string or object", example = "{\"facebook\":\"https://facebook.com/user\",\"twitter\":\"https://twitter.com/user\"}")
    @JsonDeserialize(using = SocialLinksDeserializer.class)
    String socialLinks; // JSON string, accepts both string and object

    @Schema(description = "Date of birth", example = "1990-01-01")
    LocalDate dateOfBirth;

    @Schema(description = "Major/Field of study", example = "Computer Science")
    @Size(max = ProfileConstants.MAJOR_MAX,
            message = ProfileErrorCodeConstants.MAJOR_SIZE_INVALID)
    String major;

    @Schema(description = "GitHub profile URL", example = "https://github.com/username")
    @Size(max = ProfileConstants.URL_MAX_LENGTH,
            message = ProfileErrorCodeConstants.GITHUB_SIZE_INVALID)
    @Pattern(regexp = ProfileConstants.URL_PATTERN,
            message = ProfileErrorCodeConstants.GITHUB_PATTERN_INVALID)
    String github;

    @Schema(description = "Personal website URL", example = "https://mywebsite.com")
    @Size(max = ProfileConstants.URL_MAX_LENGTH,
            message = ProfileErrorCodeConstants.WEB_SIZE_INVALID)
    @Pattern(regexp = ProfileConstants.URL_PATTERN,
            message = ProfileErrorCodeConstants.WEB_PATTERN_INVALID)
    String web;

    @Schema(description = "Verified status", example = "true")
    Boolean verified;
    
    @Schema(description = "Blocked status", example = "false")
    Boolean blocked;
    
    @Schema(description = "Active status", example = "true")
    Boolean active;
}
