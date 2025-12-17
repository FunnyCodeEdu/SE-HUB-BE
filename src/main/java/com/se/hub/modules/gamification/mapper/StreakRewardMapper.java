package com.se.hub.modules.gamification.mapper;

import com.se.hub.modules.gamification.dto.request.CreateStreakRewardRequest;
import com.se.hub.modules.gamification.dto.request.UpdateStreakRewardRequest;
import com.se.hub.modules.gamification.dto.response.StreakRewardResponse;
import com.se.hub.modules.gamification.entity.StreakReward;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        uses = {RewardMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StreakRewardMapper {
    StreakRewardResponse toStreakRewardResponse(StreakReward streakReward);
    StreakReward toStreakReward(CreateStreakRewardRequest request);
    StreakReward updateStreakRewardFromRequest(@MappingTarget StreakReward streakReward, UpdateStreakRewardRequest request);
}


