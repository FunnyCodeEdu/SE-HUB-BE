package com.catsocute.japanlearn_hub.modules.user.mapper;

import com.catsocute.japanlearn_hub.modules.user.dto.request.PermissionCreationRequest;
import com.catsocute.japanlearn_hub.modules.user.dto.response.PermissionResponse;
import com.catsocute.japanlearn_hub.modules.user.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionCreationRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
