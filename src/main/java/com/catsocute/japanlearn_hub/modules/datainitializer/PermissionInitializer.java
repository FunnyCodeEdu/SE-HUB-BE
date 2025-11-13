package com.catsocute.japanlearn_hub.modules.datainitializer;

import com.catsocute.japanlearn_hub.common.constant.InitializerOrder;
import com.catsocute.japanlearn_hub.modules.user.constant.permission.StartDefinedPermission;
import com.catsocute.japanlearn_hub.modules.user.entity.Permission;
import com.catsocute.japanlearn_hub.modules.user.repository.PermissionRepository;
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

@Order(InitializerOrder.PERMISSION)
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionInitializer implements ApplicationRunner {
    PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Initializing permission ...");

        Set<Permission> permissions = getPermissions();

        permissions.forEach(permission -> {
            if (!permissionRepository.existsById(permission.getName())) {
                permissionRepository.save(permission);
                log.info("Created permission [{}]", permission.getName());
            }
        });
        log.info("Permission initialization completed!");
    }

    private Set<Permission> getPermissions() {
        return Set.of(
                // ===== SYSTEM =====
                new Permission(StartDefinedPermission.PERMISSION_MANAGE, "Manage permissions, including create, update, and delete"),
                new Permission(StartDefinedPermission.PERMISSION_VIEW, "View the list of permissions"),
                new Permission(StartDefinedPermission.SYSTEM_MANAGE, "Full system management access with the highest privileges"),
                new Permission(StartDefinedPermission.SYSTEM_VIEW, "View system information and status"),
                new Permission(StartDefinedPermission.ROLE_MANAGE, "Manage roles, including create, update, and delete"),
                new Permission(StartDefinedPermission.ROLE_VIEW, "View the list of roles"),
                new Permission(StartDefinedPermission.AUDIT_VIEW, "View system audit logs"),

                // ===== USER =====
                new Permission(StartDefinedPermission.USER_VIEW, "View user information"),
                new Permission(StartDefinedPermission.USER_UPDATE, "Update user information"),
                new Permission(StartDefinedPermission.USER_DELETE, "Delete a user account"),

                // ===== PROFILE =====
                new Permission(StartDefinedPermission.PROFILE_UPDATE, "Update own profile information"),
                new Permission(StartDefinedPermission.PROFILE_VIEW, "View own profile information"),

                // ===== LEARNING CONTENT =====
                new Permission(StartDefinedPermission.LESSON_CREATE, "Create a new lesson"),
                new Permission(StartDefinedPermission.LESSON_VIEW, "View lesson details"),
                new Permission(StartDefinedPermission.LESSON_DELETE, "Delete a lesson"),
                new Permission(StartDefinedPermission.LESSON_UPDATE, "Update lesson information"),

                new Permission(StartDefinedPermission.QUIZ_CREATE, "Create a new quiz"),
                new Permission(StartDefinedPermission.QUIZ_VIEW, "View quiz details"),
                new Permission(StartDefinedPermission.QUIZ_DELETE, "Delete a quiz"),
                new Permission(StartDefinedPermission.QUIZ_UPDATE, "Update quiz information")
        );
    }
}
