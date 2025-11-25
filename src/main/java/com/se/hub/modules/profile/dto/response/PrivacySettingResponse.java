package com.se.hub.modules.profile.dto.response;

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
public class PrivacySettingResponse {
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

