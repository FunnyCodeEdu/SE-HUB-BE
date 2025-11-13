package com.se.hub.modules.profile.mapper;

import com.se.hub.modules.profile.dto.request.CreateUserLevelRequest;
import com.se.hub.modules.profile.dto.response.UserLevelResponse;
import com.se.hub.modules.profile.entity.UserLevel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserLevelMapper {
    UserLevel toUserLevel(CreateUserLevelRequest request);
    UserLevelResponse toUserLevelResponse(UserLevel userLevel);
}
