package com.se.hub.modules.gamification.mapper;

import com.se.hub.modules.gamification.dto.response.RewardResponse;
import com.se.hub.modules.gamification.entity.Reward;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface RewardMapper {
    RewardResponse toRewardResponse(Reward reward);
}

