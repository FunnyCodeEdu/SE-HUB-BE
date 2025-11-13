package com.se.hub.modules.datainitializer;

import com.se.hub.common.constant.InitializerOrder;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.user.constant.permission.StartDefinedPermission;
import com.se.hub.modules.user.constant.role.PredefinedRole;
import com.se.hub.modules.user.entity.Permission;
import com.se.hub.modules.user.entity.Role;
import com.se.hub.modules.user.repository.PermissionRepository;
import com.se.hub.modules.user.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Order(InitializerOrder.ROLE)
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleInitializer implements ApplicationRunner {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Initializing roles ...");

        Set<Role> roles = getRoles();

        roles.forEach(role -> {
            if (!roleRepository.existsById(role.getName())) {
                roleRepository.save(role);
                log.info("Created role [{}]", role.getName());
            }
        });
        log.info("Role initialization completed!");
    }

    private Set<Role> getRoles() {
        Role adminRole = Role.builder()
                .name(PredefinedRole.ADMIN_ROLE)
                .permissions(getAdminPermission())
                .build();
        
        Role userRole = Role.builder()
                .name(PredefinedRole.USER_ROLE)
                .permissions(getUserPermission())
                .build();
        
        return Set.of(adminRole, userRole);
    }

    private Set<Permission> getUserPermission() {
        return Set.of(
                permissionRepository.findById(StartDefinedPermission.USER_UPDATE)
                        .orElseThrow(() -> new AppException(ErrorCode.PERM_PERMISSION_NOT_EXISTED)),
                permissionRepository.findById(StartDefinedPermission.USER_DELETE)
                        .orElseThrow(() -> new AppException(ErrorCode.PERM_PERMISSION_NOT_EXISTED))
        );
    }

    private Set<Permission> getAdminPermission() {
        return Set.of(
                permissionRepository.findById(StartDefinedPermission.SYSTEM_MANAGE)
                        .orElseThrow(() -> new AppException(ErrorCode.PERM_PERMISSION_NOT_EXISTED))
        );
    }
}
