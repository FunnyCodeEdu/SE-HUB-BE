package com.se.hub.modules.gamification.dto.response;

import com.se.hub.modules.gamification.enums.SeasonStatus;
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
public class SeasonResponse {
    String id;
    Instant createDate;
    Instant updatedDate;

    String name;
    Instant startAt;
    Instant endAt;
    SeasonStatus status;
    List<RewardResponse> rewards;
}

