package com.catsocute.japanlearn_hub.modules.user.service.impl;

import com.catsocute.japanlearn_hub.common.constant.GlobalVariable;
import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.common.enums.ErrorCode;
import com.catsocute.japanlearn_hub.common.exception.AppException;
import com.catsocute.japanlearn_hub.common.utils.PagingUtil;
import com.catsocute.japanlearn_hub.modules.user.dto.request.PermissionCreationRequest;
import com.catsocute.japanlearn_hub.modules.user.dto.response.PermissionResponse;
import com.catsocute.japanlearn_hub.modules.user.entity.Permission;
import com.catsocute.japanlearn_hub.modules.user.mapper.PermissionMapper;
import com.catsocute.japanlearn_hub.modules.user.repository.PermissionRepository;
import com.catsocute.japanlearn_hub.modules.user.service.api.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionServiceImpl implements PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @Override
    public PermissionResponse create(PermissionCreationRequest request) {
        if(isPermissionExisted(request.getName())) {
            throw new AppException(ErrorCode.PERM_PERMISSION_EXISTED);
        }
        Permission permission = permissionMapper.toPermission(request);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    @Override
    public PermissionResponse getByName(String name) {
        if(!isPermissionExisted(name)) {
            throw new AppException(ErrorCode.PERM_PERMISSION_NOT_EXISTED);
        }
        return permissionMapper.toPermissionResponse(permissionRepository.findByName(name));
    }

    @Override
    public PagingResponse<PermissionResponse> getAll(PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Permission> permissionPages = permissionRepository.findAll(pageable);

        return PagingResponse.<PermissionResponse>builder()
                .currentPage(request.getPage())
                .pageSize(permissionPages.getSize())
                .totalPages(permissionPages.getTotalPages())
                .totalElement(permissionPages.getTotalElements())
                .data(permissionPages.getContent().stream()
                        .map(permissionMapper::toPermissionResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public void deleteById(String id) {
        permissionRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        permissionRepository.deleteAll();
    }

    private boolean isPermissionExisted(String name) {
        return permissionRepository.existsByName(name);
    }
}
