package com.se.hub.modules.interaction.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.interaction.dto.request.CreateReportRequest;
import com.se.hub.modules.interaction.dto.request.ReportReasonRequest;
import com.se.hub.modules.interaction.dto.request.UpdateReportRequest;
import com.se.hub.modules.interaction.dto.response.ReportResponse;
import com.se.hub.modules.interaction.dto.response.ReportSummaryResponse;
import com.se.hub.modules.interaction.entity.Report;
import com.se.hub.modules.interaction.entity.ReportReason;
import com.se.hub.modules.interaction.enums.ReportStatus;
import com.se.hub.modules.interaction.enums.ReportType;
import com.se.hub.modules.interaction.enums.TargetType;
import com.se.hub.modules.interaction.exception.InteractionErrorCode;
import com.se.hub.modules.interaction.mapper.ReportMapper;
import com.se.hub.modules.interaction.repository.ReportRepository;
import com.se.hub.modules.interaction.service.api.ReportService;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.profile.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Report Service Implementation
 * Virtual Thread Best Practice:
 * - This service uses synchronous blocking I/O operations (JPA repository calls)
 * - Virtual threads automatically handle blocking operations efficiently
 * - No need to use CompletableFuture or reactive APIs
 * - Each method call will run on a virtual thread, allowing high concurrency
 * - Database operations are blocking but virtual threads handle them efficiently
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportServiceImpl implements ReportService {

    ReportRepository reportRepository;
    ReportMapper reportMapper;
    ProfileRepository profileRepository;

    /**
     * Create a new report.
     * Virtual Thread Best Practice: Uses @Transactional with synchronous blocking I/O operations.
     * Virtual threads yield during each database operation, enabling high concurrency.
     */
    @Override
    @Transactional
    public ReportResponse createReport(CreateReportRequest request) {
        log.debug("ReportServiceImpl_createReport_Creating new report for user: {}", AuthUtils.getCurrentUserId());

        // Blocking I/O - virtual thread yields here
        String userId = AuthUtils.getCurrentUserId();
        Profile reporter = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("ReportServiceImpl_createReport_Profile for user id {} not found", userId);
                    return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                });

        // Validate targetType
        TargetType targetType;
        try {
            targetType = TargetType.valueOf(request.getTargetType().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("ReportServiceImpl_createReport_Invalid target type: {}", request.getTargetType());
            throw InteractionErrorCode.COMMENT_TARGET_TYPE_INVALID.toException();
        }

        // Check if user already reported this target
        // Blocking I/O - virtual thread yields here
        if (reportRepository.existsByReporterIdAndTargetTypeAndTargetId(userId, targetType, request.getTargetId())) {
            log.warn("ReportServiceImpl_createReport_User {} already reported target {} {}", userId, targetType, request.getTargetId());
            throw InteractionErrorCode.REPORT_ALREADY_EXISTS.toException();
        }

        // Create Report entity
        Report report = reportMapper.toReport(request);
        report.setReporter(reporter);
        report.setStatus(ReportStatus.PENDING);

        // Create ReportReason entities
        Set<ReportReason> reasons = new HashSet<>();
        for (ReportReasonRequest reasonRequest : request.getReasons()) {
            ReportReason reason = ReportReason.builder()
                    .reportType(reasonRequest.getReportType())
                    .description(reasonRequest.getDescription())
                    .report(report)
                    .build();
            reasons.add(reason);
        }
        report.setReasons(reasons);

        report.setCreatedBy(userId);
        report.setUpdateBy(userId);

        // Blocking I/O - virtual thread yields here
        ReportResponse response = reportMapper.toReportResponse(reportRepository.save(report));
        log.debug("ReportServiceImpl_createReport_Report created successfully with id: {}", response.getId());
        return response;
    }

    /**
     * Get report by ID.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operation.
     * Virtual threads yield during database query, enabling high concurrency.
     */
    @Override
    public ReportResponse getById(String reportId) {
        log.debug("ReportServiceImpl_getById_Fetching report with id: {}", reportId);
        // Blocking I/O - virtual thread yields here
        return reportMapper.toReportResponse(reportRepository.findById(reportId)
                .orElseThrow(() -> {
                    log.error("ReportServiceImpl_getById_Report id {} not found", reportId);
                    return InteractionErrorCode.REPORT_NOT_FOUND.toException();
                }));
    }

    /**
     * Get all reports with pagination.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    public PagingResponse<ReportResponse> getReports(PagingRequest request) {
        log.debug("ReportServiceImpl_getReports_Fetching reports with page: {}, size: {}", request.getPage(), request.getPageSize());

        // Check permission - only admin/staff
        checkAdminOrStaffPermission();

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        // Blocking I/O - virtual thread yields here
        Page<Report> reports = reportRepository.findAll(pageable);

        return PagingResponse.<ReportResponse>builder()
                .currentPage(reports.getNumber())
                .totalPages(reports.getTotalPages())
                .pageSize(reports.getSize())
                .totalElement(reports.getTotalElements())
                .data(reports.getContent().stream()
                        .map(reportMapper::toReportResponse)
                        .toList())
                .build();
    }

    /**
     * Get reports by target.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    public PagingResponse<ReportResponse> getReportsByTarget(String targetType, String targetId, PagingRequest request) {
        log.debug("ReportServiceImpl_getReportsByTarget_Fetching reports for target: {} {}", targetType, targetId);

        // Check permission - only admin/staff
        checkAdminOrStaffPermission();

        TargetType type;
        try {
            type = TargetType.valueOf(targetType.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("ReportServiceImpl_getReportsByTarget_Invalid target type: {}", targetType);
            throw InteractionErrorCode.COMMENT_TARGET_TYPE_INVALID.toException();
        }

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        // Blocking I/O - virtual thread yields here
        Page<Report> reports = reportRepository.findByTargetTypeAndTargetId(type, targetId, pageable);

        return PagingResponse.<ReportResponse>builder()
                .currentPage(reports.getNumber())
                .totalPages(reports.getTotalPages())
                .pageSize(reports.getSize())
                .totalElement(reports.getTotalElements())
                .data(reports.getContent().stream()
                        .map(reportMapper::toReportResponse)
                        .toList())
                .build();
    }

    /**
     * Get reports by status.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    public PagingResponse<ReportResponse> getReportsByStatus(ReportStatus status, PagingRequest request) {
        log.debug("ReportServiceImpl_getReportsByStatus_Fetching reports with status: {}", status);

        // Check permission - only admin/staff
        checkAdminOrStaffPermission();

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        // Blocking I/O - virtual thread yields here
        Page<Report> reports = reportRepository.findByStatus(status, pageable);

        return PagingResponse.<ReportResponse>builder()
                .currentPage(reports.getNumber())
                .totalPages(reports.getTotalPages())
                .pageSize(reports.getSize())
                .totalElement(reports.getTotalElements())
                .data(reports.getContent().stream()
                        .map(reportMapper::toReportResponse)
                        .toList())
                .build();
    }

    /**
     * Get reports of current user.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    public PagingResponse<ReportResponse> getMyReports(PagingRequest request) {
        log.debug("ReportServiceImpl_getMyReports_Fetching reports for user: {}", AuthUtils.getCurrentUserId());

        // Blocking I/O - virtual thread yields here
        String userId = AuthUtils.getCurrentUserId();
        Profile reporter = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("ReportServiceImpl_getMyReports_Profile for user id {} not found", userId);
                    return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                });

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        // Blocking I/O - virtual thread yields here
        Page<Report> reports = reportRepository.findByReporter(reporter, pageable);

        return PagingResponse.<ReportResponse>builder()
                .currentPage(reports.getNumber())
                .totalPages(reports.getTotalPages())
                .pageSize(reports.getSize())
                .totalElement(reports.getTotalElements())
                .data(reports.getContent().stream()
                        .map(reportMapper::toReportResponse)
                        .toList())
                .build();
    }

    /**
     * Update report status (admin/staff only).
     * Virtual Thread Best Practice: Uses @Transactional with synchronous blocking I/O operations.
     * Virtual threads yield during each database operation, enabling high concurrency.
     */
    @Override
    @Transactional
    public ReportResponse updateReportStatus(String reportId, UpdateReportRequest request) {
        log.debug("ReportServiceImpl_updateReportStatus_Updating report status for id: {} to {}", reportId, request.getStatus());

        // Check permission - only admin/staff
        checkAdminOrStaffPermission();

        // Blocking I/O - virtual thread yields here
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> {
                    log.error("ReportServiceImpl_updateReportStatus_Report id {} not found", reportId);
                    return InteractionErrorCode.REPORT_NOT_FOUND.toException();
                });

        report.setStatus(request.getStatus());
        report.setUpdateBy(AuthUtils.getCurrentUserId());

        // Blocking I/O - virtual thread yields here
        ReportResponse response = reportMapper.toReportResponse(reportRepository.save(report));
        log.debug("ReportServiceImpl_updateReportStatus_Report status updated successfully with id: {}", reportId);
        return response;
    }

    /**
     * Delete report (admin or reporter only).
     * Virtual Thread Best Practice: Uses @Transactional with synchronous blocking I/O operations.
     * Virtual threads yield during database operations, enabling high concurrency.
     */
    @Override
    @Transactional
    public void deleteReport(String reportId) {
        log.debug("ReportServiceImpl_deleteReport_Deleting report with id: {}", reportId);

        // Blocking I/O - virtual thread yields here
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> {
                    log.error("ReportServiceImpl_deleteReport_Report id {} not found", reportId);
                    return InteractionErrorCode.REPORT_NOT_FOUND.toException();
                });

        String currentUserId = AuthUtils.getCurrentUserId();
        boolean isAdminOrStaff = isAdminOrStaff();
        boolean isReporter = report.getReporter().getUser().getId().equals(currentUserId);

        if (!isAdminOrStaff && !isReporter) {
            log.error("ReportServiceImpl_deleteReport_User {} is not allowed to delete report {}", currentUserId, reportId);
            throw InteractionErrorCode.REPORT_DELETE_FORBIDDEN.toException();
        }

        reportRepository.delete(report);
        log.debug("ReportServiceImpl_deleteReport_Report deleted successfully with id: {}", reportId);
    }

    /**
     * Check if current user has reported a target.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operation.
     * Virtual threads yield during database query, enabling high concurrency.
     */
    @Override
    public boolean hasUserReported(String targetType, String targetId) {
        log.debug("ReportServiceImpl_hasUserReported_Checking if user reported target: {} {}", targetType, targetId);

        try {
            // Blocking I/O - virtual thread yields here
            String userId = AuthUtils.getCurrentUserId();
            TargetType type = TargetType.valueOf(targetType.toUpperCase());
            return reportRepository.existsByReporterIdAndTargetTypeAndTargetId(userId, type, targetId);
        } catch (Exception e) {
            log.error("ReportServiceImpl_hasUserReported_Error checking report: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get report summary (dashboard - admin/staff only).
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    public ReportSummaryResponse getReportSummary() {
        log.debug("ReportServiceImpl_getReportSummary_Fetching report summary");

        // Check permission - only admin/staff
        checkAdminOrStaffPermission();

        // Blocking I/O - virtual thread yields here
        long totalReports = reportRepository.count();
        long pendingReports = reportRepository.countByStatus(ReportStatus.PENDING);
        long approvedReports = reportRepository.countByStatus(ReportStatus.APPROVED);
        long rejectedReports = reportRepository.countByStatus(ReportStatus.REJECTED);
        long resolvedReports = reportRepository.countByStatus(ReportStatus.RESOLVED);

        // Get reports by type (need to query all and group)
        // Blocking I/O - virtual thread yields here
        Map<String, Long> reportsByType = new HashMap<>();
        for (ReportType reportType : ReportType.values()) {
            // Note: This is a simplified approach. For better performance, consider adding a query method
            long count = reportRepository.findAll().stream()
                    .flatMap(report -> report.getReasons().stream())
                    .filter(reason -> reason.getReportType() == reportType)
                    .count();
            reportsByType.put(reportType.name(), count);
        }

        return ReportSummaryResponse.builder()
                .totalReports(totalReports)
                .pendingReports(pendingReports)
                .approvedReports(approvedReports)
                .rejectedReports(rejectedReports)
                .resolvedReports(resolvedReports)
                .reportsByType(reportsByType)
                .build();
    }

    /**
     * Check if current user is admin or staff
     */
    private boolean isAdminOrStaff() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN") || authority.equals("ROLE_STAFF"));
    }

    /**
     * Check admin/staff permission and throw exception if not authorized
     */
    private void checkAdminOrStaffPermission() {
        if (!isAdminOrStaff()) {
            log.error("ReportServiceImpl_checkAdminOrStaffPermission_User {} is not admin or staff", AuthUtils.getCurrentUserId());
            throw InteractionErrorCode.FORBIDDEN_OPERATION.toException();
        }
    }
}

