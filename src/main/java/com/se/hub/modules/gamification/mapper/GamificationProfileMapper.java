package com.se.hub.modules.gamification.mapper;

import com.se.hub.modules.gamification.dto.response.GamificationProfileResponse;
import com.se.hub.modules.gamification.entity.GamificationProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GamificationProfileMapper {
    GamificationProfileResponse toGamificationProfileResponse(GamificationProfile gamificationProfile);
}

