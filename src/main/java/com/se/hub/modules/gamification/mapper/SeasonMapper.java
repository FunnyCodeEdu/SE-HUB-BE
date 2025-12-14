package com.se.hub.modules.gamification.mapper;

import com.se.hub.modules.gamification.dto.request.CreateSeasonRequest;
import com.se.hub.modules.gamification.dto.request.UpdateSeasonRequest;
import com.se.hub.modules.gamification.dto.response.SeasonResponse;
import com.se.hub.modules.gamification.entity.Season;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        uses = {RewardMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SeasonMapper {
    SeasonResponse toSeasonResponse(Season season);
    Season toSeason(CreateSeasonRequest request);
    Season updateSeasonFromRequest(@MappingTarget Season season, UpdateSeasonRequest request);
}

