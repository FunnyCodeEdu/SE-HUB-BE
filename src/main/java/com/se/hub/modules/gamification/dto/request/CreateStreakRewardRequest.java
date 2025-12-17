package com.se.hub.modules.gamification.dto.request;

import com.se.hub.modules.gamification.constant.rewardstreak.RewardStreakConstants;
import com.se.hub.modules.gamification.constant.rewardstreak.RewardStreakMessageConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateStreakRewardRequest {
    @Min(value = 0, message = RewardStreakMessageConstants.STREAK_TARGET_MIN)
    int streakTarget;

    @Size(max = RewardStreakConstants.DESCRIPTION_MAX, message = RewardStreakMessageConstants.DESCRIPTION_MAX)
    String description;

    boolean active;

    @Valid
    List<CreateRewardRequest> rewards;
}


