package com.se.hub.modules.user.mapper;

import com.se.hub.modules.user.dto.request.UserCreationRequest;
import com.se.hub.modules.user.dto.response.UserResponse;
import com.se.hub.modules.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);
}
