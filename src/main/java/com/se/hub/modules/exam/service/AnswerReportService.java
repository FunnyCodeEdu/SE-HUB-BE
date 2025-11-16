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
     * Approve answer report (admin only)
     */
    AnswerReportResponse approveAnswerReport(String reportId);

    /**
     * Reject answer report (admin only)
     */
    AnswerReportResponse rejectAnswerReport(String reportId);
}

