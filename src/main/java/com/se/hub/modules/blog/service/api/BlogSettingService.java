package com.se.hub.modules.blog.service.api;

import com.se.hub.modules.blog.dto.response.BlogSettingResponse;

public interface BlogSettingService {
    /**
     * Toggle blog approval requirement mode (Admin only)
     * @return BlogSettingResponse with current status
     */
    BlogSettingResponse toggleApprovalMode();

    /**
     * Get current blog approval requirement setting
     * @return BlogSettingResponse with current status
     */
    BlogSettingResponse getApprovalMode();

    /**
     * Check if blog approval is required
     * @return true if approval is required, false otherwise
     */
    boolean isApprovalRequired();
}

