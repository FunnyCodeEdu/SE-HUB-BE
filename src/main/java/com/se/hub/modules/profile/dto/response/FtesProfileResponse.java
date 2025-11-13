package com.se.hub.modules.profile.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

/**
 * Response DTO from FTES (FunnyCodeEdu) profile API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FtesProfileResponse {
    String id;
    String userId;
    String name;
    String email;
    String username;
    String role;
    String phoneNumber;
    String address;
    String gender;
    Date dateOfBirth;
    String avatar;
    boolean isDarkMode;
    Date createdAt;
    Date updatedAt;
    String description;
    String jobName;
    String facebook;
    String youtube;
    String twitter;
}

