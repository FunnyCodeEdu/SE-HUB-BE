package com.se.hub.modules.user.mapper;

import com.se.hub.modules.user.dto.response.UserResponse;
import com.se.hub.modules.user.entity.Role;
import com.se.hub.modules.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "role", target = "role", qualifiedByName = "mapRoleName")
    UserResponse toUserResponse(User user);
    
    @Named("mapRoleName")
    default String mapRoleName(Role role) {
        return role != null ? role.getName() : null;
    }
}
