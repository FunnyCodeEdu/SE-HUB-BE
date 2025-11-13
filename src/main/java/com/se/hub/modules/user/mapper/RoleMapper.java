package com.se.hub.modules.user.mapper;

import com.se.hub.modules.user.dto.response.RoleResponse;
import com.se.hub.modules.user.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PermissionMapper.class)
public interface RoleMapper {
    @Mapping(source = "name", target = "roleName")
    @Mapping(target = "description", ignore = true) // Role entity doesn't have description field
    RoleResponse toRoleResponse(Role role);
}
