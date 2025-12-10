package com.se.hub.modules.gamification.mapper;

import com.se.hub.modules.gamification.dto.response.MissionResponse;
import com.se.hub.modules.gamification.entity.Mission;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {RewardMapper.class})
public interface MissionMapper {
    MissionResponse toMissionResponse(Mission mission);
}

