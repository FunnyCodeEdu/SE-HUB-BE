package com.se.hub.modules.gamification.dto.response;

import com.se.hub.modules.gamification.enums.MissionTargetType;
import com.se.hub.modules.gamification.enums.MissionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MissionResponse {
    String id;
    Instant createDate;
    Instant updatedDate;

    MissionType type;
    boolean active;
    MissionTargetType targetType;
    int totalCount;
    String description;
    List<RewardResponse> rewards;
}

