package com.se.hub.modules.datainitializer;

import com.se.hub.common.constant.InitializerOrder;
import com.se.hub.modules.user.constant.role.PredefinedRole;
import com.se.hub.modules.user.entity.Role;
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
                    log.debug("RoleInitializer_run_Created role: {}", role.getName());
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
     * Get all predefined roles
     * 
     * @return Set of roles to initialize
     */
    private Set<Role> getRoles() {
        Role adminRole = Role.builder()
                .name(PredefinedRole.ADMIN_ROLE)
                .build();
        
        Role userRole = Role.builder()
                .name(PredefinedRole.USER_ROLE)
                .build();
        
        return Set.of(adminRole, userRole);
    }
}
