package com.se.hub.modules.user.mapper;

import com.se.hub.modules.user.dto.response.RoleResponse;
import com.se.hub.modules.user.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toRoleResponse(Role role);
}
