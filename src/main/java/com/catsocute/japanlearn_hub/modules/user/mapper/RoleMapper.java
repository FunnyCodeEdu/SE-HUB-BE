package com.catsocute.japanlearn_hub.modules.user.mapper;

import com.catsocute.japanlearn_hub.modules.user.dto.response.RoleResponse;
import com.catsocute.japanlearn_hub.modules.user.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toRoleResponse(Role role);
}
