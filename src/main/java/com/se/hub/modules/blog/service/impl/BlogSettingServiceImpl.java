package com.se.hub.modules.blog.service.impl;

import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.blog.entity.BlogSetting;
import com.se.hub.modules.blog.dto.response.BlogSettingResponse;
import com.se.hub.modules.blog.exception.BlogErrorCode;
import com.se.hub.modules.blog.repository.BlogSettingRepository;
import com.se.hub.modules.blog.service.api.BlogSettingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Blog Setting Service Implementation
 * Virtual Thread Best Practice:
 * - Uses synchronous blocking I/O operations (JPA repository calls)
 * - Virtual threads automatically handle blocking operations efficiently
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlogSettingServiceImpl implements BlogSettingService {

    BlogSettingRepository blogSettingRepository;

    /**
     * Get or create singleton blog setting
     */
    private BlogSetting getOrCreateSetting() {
        return blogSettingRepository.findById(BlogSetting.SINGLETON_ID)
                .orElseGet(() -> {
                    log.info("BlogSettingServiceImpl_getOrCreateSetting_Creating default blog setting");
                    BlogSetting defaultSetting = BlogSetting.builder()
                            .requireApproval(true)
                            .build();
                    defaultSetting.setId(BlogSetting.SINGLETON_ID);
                    defaultSetting.setCreatedBy("SYSTEM");
                    defaultSetting.setUpdateBy("SYSTEM");
                    return blogSettingRepository.save(defaultSetting);
                });
    }

    /**
     * Toggle blog approval requirement mode (Admin only)
     * Virtual Thread Best Practice: Uses @Transactional with synchronous blocking I/O operations.
     */
    @Override
    @Transactional
    public BlogSettingResponse toggleApprovalMode() {
        log.debug("BlogSettingServiceImpl_toggleApprovalMode_Toggling approval mode");
        
        checkAdminPermission();
        
        BlogSetting setting = getOrCreateSetting();
        setting.setRequireApproval(!setting.getRequireApproval());
        setting.setUpdateBy(AuthUtils.getCurrentUserId());
        
        BlogSetting savedSetting = blogSettingRepository.save(setting);
        
        log.info("BlogSettingServiceImpl_toggleApprovalMode_Approval mode toggled to: {}", savedSetting.getRequireApproval());
        
        return BlogSettingResponse.builder()
                .requireApproval(savedSetting.getRequireApproval())
                .build();
    }

    /**
     * Check admin permission and throw exception if not authorized
     */
    private void checkAdminPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("BlogSettingServiceImpl_checkAdminPermission_User is not authenticated");
            throw BlogErrorCode.BLOG_FORBIDDEN_OPERATION.toException();
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            log.error("BlogSettingServiceImpl_checkAdminPermission_User {} is not admin", AuthUtils.getCurrentUserId());
            throw BlogErrorCode.BLOG_FORBIDDEN_OPERATION.toException();
        }
    }

    /**
     * Get current blog approval requirement setting
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operation.
     */
    @Override
    public BlogSettingResponse getApprovalMode() {
        log.debug("BlogSettingServiceImpl_getApprovalMode_Getting approval mode");
        
        BlogSetting setting = getOrCreateSetting();
        
        return BlogSettingResponse.builder()
                .requireApproval(setting.getRequireApproval())
                .build();
    }

    /**
     * Check if blog approval is required
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operation.
     */
    @Override
    public boolean isApprovalRequired() {
        BlogSetting setting = getOrCreateSetting();
        return setting.getRequireApproval();
    }
}

