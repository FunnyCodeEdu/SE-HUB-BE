package com.se.hub.modules.notification.dto.request;

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
public class UpdateNotificationSettingRequest {
    Boolean emailEnabled;
    Boolean pushEnabled;
    Boolean mentionEnabled;
    Boolean likeEnabled;
    Boolean commentEnabled;
    Boolean blogEnabled;
    Boolean achievementEnabled;
    Boolean followEnabled;
    Boolean systemEnabled;
}


