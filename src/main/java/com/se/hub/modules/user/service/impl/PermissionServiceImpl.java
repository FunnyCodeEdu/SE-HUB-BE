package com.se.hub.modules.user.service.impl;

import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.user.dto.request.PermissionCreationRequest;
import com.se.hub.modules.user.dto.response.PermissionResponse;
import com.se.hub.modules.user.entity.Permission;
import com.se.hub.modules.user.mapper.PermissionMapper;
import com.se.hub.modules.user.repository.PermissionRepository;
import com.se.hub.modules.user.service.api.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionServiceImpl implements PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @Override
    @Transactional
    public PermissionResponse create(PermissionCreationRequest request) {
        if (permissionRepository.existsById(request.getName())) {
            throw new AppException(ErrorCode.PERM_PERMISSION_EXISTED);
        }
        
        Permission permission = permissionMapper.toPermission(request);
        Permission saved = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(saved);
    }

    @Override
    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }

    @Override
    public PermissionResponse getByName(String name) {
        Permission permission = permissionRepository.findById(name)
                .orElseThrow(() -> new AppException(ErrorCode.PERM_PERMISSION_NOT_EXISTED));
        return permissionMapper.toPermissionResponse(permission);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        if (!permissionRepository.existsById(id)) {
            throw new AppException(ErrorCode.PERM_PERMISSION_NOT_EXISTED);
        }
        permissionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAll() {
        permissionRepository.deleteAll();
    }
}

