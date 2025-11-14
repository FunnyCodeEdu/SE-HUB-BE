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
        log.info("RoleInitializer_run_Starting role initialization");
        
        try {
            Set<Role> roles = getRoles();
            int createdCount = 0;
            int skippedCount = 0;

            for (Role role : roles) {
                if (!roleRepository.existsById(role.getName())) {
                    roleRepository.save(role);
                    createdCount++;
                    log.debug("RoleInitializer_run_Created role: {} with {} permissions", 
                            role.getName(), role.getPermissions() != null ? role.getPermissions().size() : 0);
                } else {
                    skippedCount++;
                    log.debug("RoleInitializer_run_Role already exists, skipped: {}", role.getName());
                }
            }
            
            log.info("RoleInitializer_run_Role initialization completed. Created: {}, Skipped: {}, Total: {}", 
                    createdCount, skippedCount, roles.size());
        } catch (Exception e) {
            log.error("RoleInitializer_run_Error during role initialization", e);
            throw e;
        }
    }

    /**
     * Get all predefined roles with their permissions
     * 
     * @return Set of roles to initialize
     */
    private Set<Role> getRoles() {
        Role adminRole = Role.builder()
                .name(PredefinedRole.ADMIN_ROLE)
                .permissions(getAdminPermissions())
                .build();
        
        Role userRole = Role.builder()
                .name(PredefinedRole.USER_ROLE)
                .permissions(getUserPermissions())
                .build();
        
        return Set.of(adminRole, userRole);
    }

    /**
     * Get permissions for USER role
     * 
     * @return Set of permissions for USER role
     * @throws AppException if required permissions are not found
     */
    private Set<Permission> getUserPermissions() {
        log.debug("RoleInitializer_getUserPermissions_Loading permissions for USER role");
        Permission userUpdate = permissionRepository.findById(StartDefinedPermission.USER_UPDATE)
                .orElseThrow(() -> {
                    log.error("RoleInitializer_getUserPermissions_Permission not found: {}", StartDefinedPermission.USER_UPDATE);
                    return new AppException(ErrorCode.PERM_PERMISSION_NOT_EXISTED);
                });
        
        Permission userDelete = permissionRepository.findById(StartDefinedPermission.USER_DELETE)
                .orElseThrow(() -> {
                    log.error("RoleInitializer_getUserPermissions_Permission not found: {}", StartDefinedPermission.USER_DELETE);
                    return new AppException(ErrorCode.PERM_PERMISSION_NOT_EXISTED);
                });
        
        return Set.of(userUpdate, userDelete);
    }

    /**
     * Get permissions for ADMIN role
     * 
     * @return Set of permissions for ADMIN role
     * @throws AppException if required permissions are not found
     */
    private Set<Permission> getAdminPermissions() {
        log.debug("RoleInitializer_getAdminPermissions_Loading permissions for ADMIN role");
        Permission systemManage = permissionRepository.findById(StartDefinedPermission.SYSTEM_MANAGE)
                .orElseThrow(() -> {
                    log.error("RoleInitializer_getAdminPermissions_Permission not found: {}", StartDefinedPermission.SYSTEM_MANAGE);
                    return new AppException(ErrorCode.PERM_PERMISSION_NOT_EXISTED);
                });
        
        return Set.of(systemManage);
    }
}
