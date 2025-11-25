package com.se.hub.modules.profile.dto.request;

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
public class UpdateSettingsRequest {
    // Notification settings
    Boolean emailEnabled;
    Boolean pushEnabled;
    Boolean mentionEnabled;
    Boolean likeEnabled;
    Boolean commentEnabled;
    Boolean blogEnabled;
    Boolean achievementEnabled;
    Boolean followEnabled;
    Boolean systemEnabled;
    
    // Privacy settings
    Boolean profilePublic;
    Boolean emailVisible;
    Boolean phoneVisible;
    Boolean addressVisible;
    Boolean dateOfBirthVisible;
    Boolean majorVisible;
    Boolean bioVisible;
    Boolean socialMediaVisible;
    Boolean achievementsVisible;
    Boolean statsVisible;
}

