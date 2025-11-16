package com.se.hub.modules.exam.service;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.exam.dto.request.CreateAnswerReportRequest;
import com.se.hub.modules.exam.dto.response.AnswerReportResponse;

public interface AnswerReportService {
    /**
     * Create new answer report
     */
    AnswerReportResponse createAnswerReport(CreateAnswerReportRequest request);

    /**
     * Get all answer reports (admin only)
     */
    PagingResponse<AnswerReportResponse> getAllAnswerReports(PagingRequest request);

    /**
     * Update answer report status (admin only)
     * Can update status to APPROVED (treated as UPDATED) or REJECTED
     */
    AnswerReportResponse updateAnswerReportStatus(String reportId, AnswerReportStatus status);
}

