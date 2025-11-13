package com.catsocute.japanlearn_hub.modules.user.service.api;

import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.modules.user.dto.request.RoleCreationRequest;
import com.catsocute.japanlearn_hub.modules.user.dto.request.RoleUpdateRequest;
import com.catsocute.japanlearn_hub.modules.user.dto.response.RoleResponse;

public interface RoleService {
    /**
     * create new role
     * @author catsocute
     */
    RoleResponse create(RoleCreationRequest request);

    /**
     * get role by name
     * @author catsocute
     */
    RoleResponse getByName(String roleName);

    /**
     * get all roles
     * @author catsocute
     */
    PagingResponse<RoleResponse> getAll(PagingRequest request);

    /**
     * update role by id(role name)
     * @author catsocute
     */
    RoleResponse updateById(String id, RoleUpdateRequest request);

    /**
     * delete role by name
     * @author catsocute
     */
    void deleteByName(String name);

    /**
     * delete all roles
     * @author catsocute
     */
    void deleteAll();
}
