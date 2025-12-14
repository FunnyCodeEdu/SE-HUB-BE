package com.se.hub.modules.gamification.mapper;

import com.se.hub.modules.gamification.dto.request.CreateRewardRequest;
import com.se.hub.modules.gamification.dto.response.RewardResponse;
import com.se.hub.modules.gamification.entity.Reward;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RewardMapper {
    RewardResponse toRewardResponse(Reward reward);
    Reward toReward(CreateRewardRequest request);
}

