package com.se.hub.modules.gamification.mapper;

import com.se.hub.modules.gamification.dto.response.StreakResponse;
import com.se.hub.modules.gamification.entity.Streak;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StreakMapper {
    StreakResponse toStreakResponse(Streak streak);
}

