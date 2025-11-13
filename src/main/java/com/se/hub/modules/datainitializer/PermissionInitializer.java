package com.se.hub.modules.datainitializer;

import com.se.hub.common.constant.InitializerOrder;
import com.se.hub.modules.user.constant.permission.StartDefinedPermission;
import com.se.hub.modules.user.entity.Permission;
import com.se.hub.modules.user.repository.PermissionRepository;
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
                Permission.builder()
                        .name(StartDefinedPermission.PERMISSION_MANAGE)
                        .description("Manage permissions, including create, update, and delete")
                        .build(),
                Permission.builder()
                        .name(StartDefinedPermission.PERMISSION_VIEW)
                        .description("View the list of permissions")
                        .build(),
                Permission.builder()
                        .name(StartDefinedPermission.SYSTEM_MANAGE)
                        .description("Full system management access with the highest privileges")
                        .build(),
                Permission.builder()
                        .name(StartDefinedPermission.SYSTEM_VIEW)
                        .description("View system information and status")
                        .build(),
                Permission.builder()
                        .name(StartDefinedPermission.ROLE_MANAGE)
                        .description("Manage roles, including create, update, and delete")
                        .build(),
                Permission.builder()
                        .name(StartDefinedPermission.ROLE_VIEW)
                        .description("View the list of roles")
                        .build(),
                Permission.builder()
                        .name(StartDefinedPermission.AUDIT_VIEW)
                        .description("View system audit logs")
                        .build(),

                // ===== USER =====
                Permission.builder()
                        .name(StartDefinedPermission.USER_VIEW)
                        .description("View user information")
                        .build(),
                Permission.builder()
                        .name(StartDefinedPermission.USER_UPDATE)
                        .description("Update user information")
                        .build(),
                Permission.builder()
                        .name(StartDefinedPermission.USER_DELETE)
                        .description("Delete a user account")
                        .build(),

                // ===== PROFILE =====
                Permission.builder()
                        .name(StartDefinedPermission.PROFILE_UPDATE)
                        .description("Update own profile information")
                        .build(),
                Permission.builder()
                        .name(StartDefinedPermission.PROFILE_VIEW)
                        .description("View own profile information")
                        .build(),

                // ===== LEARNING CONTENT =====
                Permission.builder()
                        .name(StartDefinedPermission.LESSON_CREATE)
                        .description("Create a new lesson")
                        .build(),
                Permission.builder()
                        .name(StartDefinedPermission.LESSON_VIEW)
                        .description("View lesson details")
                        .build(),
                Permission.builder()
                        .name(StartDefinedPermission.LESSON_DELETE)
                        .description("Delete a lesson")
                        .build(),
                Permission.builder()
                        .name(StartDefinedPermission.LESSON_UPDATE)
                        .description("Update lesson information")
                        .build(),

                Permission.builder()
                        .name(StartDefinedPermission.QUIZ_CREATE)
                        .description("Create a new quiz")
                        .build(),
                Permission.builder()
                        .name(StartDefinedPermission.QUIZ_VIEW)
                        .description("View quiz details")
                        .build(),
                Permission.builder()
                        .name(StartDefinedPermission.QUIZ_DELETE)
                        .description("Delete a quiz")
                        .build(),
                Permission.builder()
                        .name(StartDefinedPermission.QUIZ_UPDATE)
                        .description("Update quiz information")
                        .build()
        );
    }
}
