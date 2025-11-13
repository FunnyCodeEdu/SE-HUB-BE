package com.se.hub.modules.datainitializer;

import com.se.hub.common.constant.InitializerOrder;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.profile.dto.request.CreateDefaultProfileRequest;
import com.se.hub.modules.profile.service.api.ProfileService;
import com.se.hub.modules.user.constant.role.PredefinedRole;
import com.se.hub.modules.user.entity.Role;
import com.se.hub.modules.user.entity.User;
import com.se.hub.modules.user.enums.UserStatus;
import com.se.hub.modules.user.repository.RoleRepository;
import com.se.hub.modules.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * AdminInitializer is disabled because users are now created via SSO (FTES).
 * Users are created when they first authenticate via JWT token.
 * This initializer is kept for reference but does not create any users.
 */
@Order(InitializerOrder.ADMIN)
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AdminInitializer implements ApplicationRunner {
    UserRepository userRepository;
    RoleRepository roleRepository;
    ProfileService profileService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("AdminInitializer: Users are now created via SSO (FTES). No local admin user creation needed.");
        // Note: Users are created when they first authenticate via JWT token from FTES
        // If you need to assign ADMIN role to an existing user, use the UserManagementController
    }
}
