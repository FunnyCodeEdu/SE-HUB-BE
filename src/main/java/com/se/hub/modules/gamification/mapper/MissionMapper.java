package com.se.hub.modules.gamification.mapper;

import com.se.hub.modules.gamification.dto.request.CreateMissionRequest;
import com.se.hub.modules.gamification.dto.request.UpdateMissionRequest;
import com.se.hub.modules.gamification.dto.response.MissionResponse;
import com.se.hub.modules.gamification.entity.Mission;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        uses = {RewardMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MissionMapper {
    MissionResponse toMissionResponse(Mission mission);
    Mission toMission(CreateMissionRequest request);
    Mission updateMissionFromRequest(@MappingTarget Mission mission, UpdateMissionRequest request);
}

