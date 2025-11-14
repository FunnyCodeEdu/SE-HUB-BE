package com.se.hub.modules.profile.dto.response;

import com.se.hub.modules.profile.enums.GenderEnums;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

/**
 * Profile response DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
    //profile information
    String id;
    Instant createDate;
    Instant updatedDate;
    String createdBy;
    String updateBy;
    String fullName;
    String phoneNum;
    String email;
    String avtUrl;
    GenderEnums gender;
    String bio;
    String address;
    String website;
    LocalDate dateOfBirth;
    String major;
    String github;
    String web;

    //profile status flags
    boolean verified;
    boolean blocked;
    boolean active;

    //user information
    String userId;
    String username;
    Set<String> userRole;
    String userStatus;

    //nested objects
    UserLevelResponse level;
    UserStatsResponse userStats;
    Set<AchievementResponse> achievements;
}
