package com.se.hub.modules.gamification.dto.response;

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
public class ClaimedStreakRewardResponse {
    String id;
    String createdBy;
    String updateBy;
    Instant createDate;
    Instant updatedDate;

    Instant claimedAt;
    StreakRewardResponse streakReward;
}

