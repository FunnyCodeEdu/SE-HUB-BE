package com.se.hub.modules.ranking.mapper;

import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.ranking.dto.response.ProfileRankingResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RankingMapper {
    ProfileRankingResponse toProfileRankingResponse(Profile profile);
}
