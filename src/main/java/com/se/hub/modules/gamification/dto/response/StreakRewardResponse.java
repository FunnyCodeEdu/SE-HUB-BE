package com.se.hub.modules.gamification.dto.response;

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
public class StreakRewardResponse {
    String id;
    Instant createDate;
    Instant updatedDate;

    int streakTarget;
    String description;
    boolean active;
    List<RewardResponse> rewards;
}

