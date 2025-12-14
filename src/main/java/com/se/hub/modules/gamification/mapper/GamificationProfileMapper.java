package com.se.hub.modules.gamification.mapper;

import com.se.hub.modules.gamification.dto.response.GamificationProfileResponse;
import com.se.hub.modules.gamification.entity.GamificationProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = {StreakMapper.class, MissionProgressMapper.class})
public interface GamificationProfileMapper {
    GamificationProfileResponse toGamificationProfileResponse(GamificationProfile gamificationProfile);
}

