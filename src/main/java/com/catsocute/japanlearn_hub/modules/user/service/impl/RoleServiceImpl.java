package com.catsocute.japanlearn_hub.modules.user.service.impl;

import com.catsocute.japanlearn_hub.common.constant.GlobalVariable;
import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.common.enums.ErrorCode;
import com.catsocute.japanlearn_hub.common.exception.AppException;
import com.catsocute.japanlearn_hub.common.utils.PagingUtil;
import com.catsocute.japanlearn_hub.modules.user.dto.request.RoleCreationRequest;
import com.catsocute.japanlearn_hub.modules.user.dto.request.RoleUpdateRequest;
import com.catsocute.japanlearn_hub.modules.user.dto.response.RoleResponse;
import com.catsocute.japanlearn_hub.modules.user.entity.Permission;
import com.catsocute.japanlearn_hub.modules.user.entity.Role;
import com.catsocute.japanlearn_hub.modules.user.mapper.RoleMapper;
import com.catsocute.japanlearn_hub.modules.user.repository.PermissionRepository;
import com.catsocute.japanlearn_hub.modules.user.repository.RoleRepository;
import com.catsocute.japanlearn_hub.modules.user.service.api.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    @Override
    public RoleResponse create(RoleCreationRequest request) {
        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .permissions(getPermission(request.getPermissions().stream().toList()))
                .build();
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    @Override
    public RoleResponse getByName(String roleName) {
        if(!isRoleExisted(roleName)) {
            throw new AppException(ErrorCode.ROLE_NOT_EXISTED);
        }
        return roleMapper.toRoleResponse(roleRepository.findById(roleName)
        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND)));
    }

    @Override
    public PagingResponse<RoleResponse> getAll(PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Role> rolePages = roleRepository.findAll(pageable);

        return PagingResponse.<RoleResponse>builder()
                .currentPage(request.getPage())
                .pageSize(rolePages.getSize())
                .totalPages(rolePages.getTotalPages())
                .totalElement(rolePages.getTotalElements())
                .data(rolePages.getContent().stream()
                        .map(roleMapper::toRoleResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public RoleResponse updateById(String id, RoleUpdateRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    @Override
    public void deleteByName(String name) {
        roleRepository.deleteById(name);
    }

    @Override
    public void deleteAll() {
        roleRepository.deleteAll();
    }

    private boolean isRoleExisted(String id) {
        return roleRepository.existsById(id);
    }

    private Set<Permission> getPermission(List<String> permissionNames) {
        return new HashSet<>(permissionRepository.findAllById(permissionNames));
    }
}
