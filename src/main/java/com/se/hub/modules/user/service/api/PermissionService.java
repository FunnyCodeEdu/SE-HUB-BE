package com.se.hub.modules.user.service.api;

import com.se.hub.modules.user.dto.request.PermissionCreationRequest;
import com.se.hub.modules.user.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    /**
     * Create permission
     */
    PermissionResponse create(PermissionCreationRequest request);

    /**
     * Get all permissions
     */
    List<PermissionResponse> getAll();

    /**
     * Get permission by name
     */
    PermissionResponse getByName(String name);

    /**
     * Delete permission by id
     */
    void deleteById(String id);

    /**
     * Delete all permissions
     */
    void deleteAll();
}

