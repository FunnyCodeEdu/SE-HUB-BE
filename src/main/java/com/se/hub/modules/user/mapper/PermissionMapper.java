package com.se.hub.modules.user.mapper;

import com.se.hub.modules.user.dto.request.PermissionCreationRequest;
import com.se.hub.modules.user.dto.response.PermissionResponse;
import com.se.hub.modules.user.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionCreationRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
