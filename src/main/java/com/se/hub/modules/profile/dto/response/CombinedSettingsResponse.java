package com.se.hub.modules.profile.dto.response;

import com.se.hub.modules.notification.dto.response.NotificationSettingResponse;
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
public class CombinedSettingsResponse {
    NotificationSettingResponse notificationSettings;
    PrivacySettingResponse privacySettings;
}

