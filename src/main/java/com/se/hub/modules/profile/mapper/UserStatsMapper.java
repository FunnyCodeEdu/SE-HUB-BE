package com.se.hub.modules.profile.mapper;

import com.se.hub.modules.profile.dto.response.UserStatsResponse;
import com.se.hub.modules.profile.entity.UserStats;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStatsMapper {
    @Mapping(source = "docsUploaded", target = "documentsUploaded")
    UserStatsResponse toUserStatsResponse(UserStats userStats);

    @Mapping(source = "documentsUploaded", target = "docsUploaded")
    @Mapping(target = "profile", ignore = true)
    UserStats fromUserStatsResponse(UserStatsResponse userStatsResponse);
}
