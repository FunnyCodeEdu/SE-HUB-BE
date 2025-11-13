package com.catsocute.japanlearn_hub.modules.profile.mapper;

import com.catsocute.japanlearn_hub.modules.profile.dto.request.CreateUserLevelRequest;
import com.catsocute.japanlearn_hub.modules.profile.dto.response.UserLevelResponse;
import com.catsocute.japanlearn_hub.modules.profile.entity.UserLevel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserLevelMapper {
    UserLevel toUserLevel(CreateUserLevelRequest request);
    UserLevelResponse toUserLevelResponse(UserLevel userLevel);
}
