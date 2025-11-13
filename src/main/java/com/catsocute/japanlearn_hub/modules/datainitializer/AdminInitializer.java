package com.catsocute.japanlearn_hub.modules.datainitializer;

import com.catsocute.japanlearn_hub.common.constant.InitializerOrder;
import com.catsocute.japanlearn_hub.common.enums.ErrorCode;
import com.catsocute.japanlearn_hub.common.exception.AppException;
import com.catsocute.japanlearn_hub.modules.configuration.AdminProperties;
import com.catsocute.japanlearn_hub.modules.profile.dto.request.CreateDefaultProfileRequest;
import com.catsocute.japanlearn_hub.modules.profile.service.api.ProfileService;
import com.catsocute.japanlearn_hub.modules.user.constant.role.PredefinedRole;
import com.catsocute.japanlearn_hub.modules.user.dto.request.UserCreationRequest;
import com.catsocute.japanlearn_hub.modules.user.entity.Role;
import com.catsocute.japanlearn_hub.modules.user.entity.User;
import com.catsocute.japanlearn_hub.modules.user.enums.UserStatus;
import com.catsocute.japanlearn_hub.modules.user.repository.RoleRepository;
import com.catsocute.japanlearn_hub.modules.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Order(InitializerOrder.ADMIN)
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AdminInitializer implements ApplicationRunner {
    UserRepository userRepository;
    RoleRepository roleRepository;
    ProfileService  profileService;
    PasswordEncoder passwordEncoder;
    AdminProperties  adminProperties;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Initializing admin ...");
        //check admin existed
        boolean existed = userRepository.existsByUsername(adminProperties.getUsername());
        if (!existed) {
            //set user role = ADMIN
            Role role = roleRepository.findById(PredefinedRole.ADMIN_ROLE)
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

            User admin = User.builder()
                    .username(adminProperties.getUsername())
                    .password(passwordEncoder.encode(adminProperties.getPassword()))
                    .roles(Set.of(role))
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(admin);

            //create default profile for admin
            profileService.createDefaultProfile(admin, createDefaultProfileRequest());

            log.info("Admin has been created with username {}", admin.getUsername());
            log.info("Admin has been created with password {}", passwordEncoder.encode(admin.getPassword()));
        }
    }

    private CreateDefaultProfileRequest createDefaultProfileRequest() {
        return CreateDefaultProfileRequest.builder()
                .request(UserCreationRequest.builder()
                        .username(adminProperties.getUsername())
                        .fullName("ADMIN")
                        .password(passwordEncoder.encode(adminProperties.getPassword()))
                        .build())
                .build();
    }
}
