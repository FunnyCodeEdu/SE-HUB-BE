package com.se.hub.modules.user.mapper;

import com.se.hub.modules.user.dto.request.PermissionCreationRequest;
import com.se.hub.modules.user.dto.response.PermissionResponse;
import com.se.hub.modules.user.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PermissionMapper {
    Permission toPermission(PermissionCreationRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}

