package com.se.hub.modules.gamification.dto.response;

import com.se.hub.modules.gamification.enums.ActionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GamificationEventLogResponse {
    String id;
    Instant createDate;
    Instant updatedDate;

    ActionType actionType;
    Long xpDelta;
    Long tokenDelta;
}

