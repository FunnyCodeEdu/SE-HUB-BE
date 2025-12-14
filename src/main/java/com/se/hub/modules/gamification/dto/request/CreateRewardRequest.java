package com.se.hub.modules.gamification.dto.request;

import com.se.hub.modules.gamification.constant.reward.RewardMessageConstants;
import com.se.hub.modules.gamification.enums.RewardType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class CreateRewardRequest {

    @NotNull(message = RewardMessageConstants.REWARD_TYPE_REQUIRED)
    RewardType rewardType;

    @NotNull(message = RewardMessageConstants.REWARD_VALUE_REQUIRED)
    @Min(value = 0, message = RewardMessageConstants.REWARD_VALUE_MIN)
    Long rewardValue;
}