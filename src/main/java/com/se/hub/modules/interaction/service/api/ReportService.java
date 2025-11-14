package com.se.hub.modules.interaction.service.api;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.interaction.dto.request.CreateReportRequest;
import com.se.hub.modules.interaction.dto.request.UpdateReportRequest;
import com.se.hub.modules.interaction.dto.response.ReportResponse;
import com.se.hub.modules.interaction.dto.response.ReportSummaryResponse;
import com.se.hub.modules.interaction.enums.ReportStatus;

public interface ReportService {

    /**
     * Create a new report
     * Virtual Thread: Uses @Transactional with blocking I/O operations
     */
    ReportResponse createReport(CreateReportRequest request);

    /**
     * Get report by ID
     * Virtual Thread: Uses blocking I/O operation
     */
    ReportResponse getById(String reportId);

    /**
     * Get all reports with pagination
     * Virtual Thread: Uses blocking I/O operations
     */
    PagingResponse<ReportResponse> getReports(PagingRequest request);

    /**
     * Get reports by target
     * Virtual Thread: Uses blocking I/O operations
     */
    PagingResponse<ReportResponse> getReportsByTarget(String targetType, String targetId, PagingRequest request);

    /**
     * Get reports by status
     * Virtual Thread: Uses blocking I/O operations
     */
    PagingResponse<ReportResponse> getReportsByStatus(ReportStatus status, PagingRequest request);

    /**
     * Get reports of current user
     * Virtual Thread: Uses blocking I/O operations
     */
    PagingResponse<ReportResponse> getMyReports(PagingRequest request);

    /**
     * Update report status (admin/staff only)
     * Virtual Thread: Uses @Transactional with blocking I/O operations
     */
    ReportResponse updateReportStatus(String reportId, UpdateReportRequest request);

    /**
     * Delete report (admin or reporter only)
     * Virtual Thread: Uses @Transactional with blocking I/O operations
     */
    void deleteReport(String reportId);

    /**
     * Check if current user has reported a target
     * Virtual Thread: Uses blocking I/O operation
     */
    boolean hasUserReported(String targetType, String targetId);

    /**
     * Get report summary (dashboard - admin/staff only)
     * Virtual Thread: Uses blocking I/O operations
     */
    ReportSummaryResponse getReportSummary();
}

