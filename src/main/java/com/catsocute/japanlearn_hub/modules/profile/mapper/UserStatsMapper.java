package com.catsocute.japanlearn_hub.modules.profile.mapper;

import com.catsocute.japanlearn_hub.modules.profile.dto.response.UserStatsResponse;
import com.catsocute.japanlearn_hub.modules.profile.entity.UserStats;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserStatsMapper {
    UserStatsResponse toUserStatsResponse(UserStats userStats);
    UserStats fromUserStatsResponse(UserStatsResponse userStatsResponse);
}
