package com.se.hub.modules.gamification.mapper;

import com.se.hub.modules.gamification.dto.response.MissionProgressResponse;
import com.se.hub.modules.gamification.entity.MissionProgress;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {MissionMapper.class})
public interface MissionProgressMapper {
    MissionProgressResponse toMissionProgressResponse(MissionProgress missionProgress);
}

