package com.catsocute.japanlearn_hub.modules.ranking.mapper;

import com.catsocute.japanlearn_hub.modules.profile.entity.Profile;
import com.catsocute.japanlearn_hub.modules.ranking.dto.response.ProfileRankingResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RankingMapper {
    ProfileRankingResponse toProfileRankingResponse(Profile profile);
}
