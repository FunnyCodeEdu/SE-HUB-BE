package com.catsocute.japanlearn_hub.modules.user.service.api;

import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.modules.user.dto.request.PermissionCreationRequest;
import com.catsocute.japanlearn_hub.modules.user.dto.response.PermissionResponse;

public interface PermissionService {
    /**
     * create new permission
     * @author catsocute
     */
    PermissionResponse create(PermissionCreationRequest request);

    /**
     * get permission by id
     * @author catsocute
     */
    PermissionResponse getByName(String name);

    /**
     * get permissions
     * @author catsocute
     */
    PagingResponse<PermissionResponse> getAll(PagingRequest request);

    /**
     * delete permission
     * @author catsocute
     */
    void deleteById(String id);

    /**
     * delete all permissions
     * @author catsocute
     */
    void deleteAll();
}
