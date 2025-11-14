package com.se.hub.modules.interaction.controller;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.constant.PaginationConstants;
import com.se.hub.common.constant.ResponseCode;
import com.se.hub.common.controller.BaseController;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.request.SortRequest;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.interaction.constant.InteractionMessageConstants;
import com.se.hub.modules.interaction.dto.request.CreateReportRequest;
import com.se.hub.modules.interaction.dto.request.UpdateReportRequest;
import com.se.hub.modules.interaction.dto.response.ReportResponse;
import com.se.hub.modules.interaction.dto.response.ReportSummaryResponse;
import com.se.hub.modules.interaction.enums.ReportStatus;
import com.se.hub.modules.interaction.service.api.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Tag(name = "Report Management",
        description = "APIs for managing reports on comments, blogs, documents, and exams")
@RequestMapping("/reports")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportController extends BaseController {

    ReportService reportService;

    @PostMapping
    @Operation(summary = "Create new report",
            description = "Create a new report for a target. Target types: BLOG, QUESTION, COURSE, LESSON, COMMENT, EXAM, PRACTICAL_EXAM, DOCUMENT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_REPORT_CREATED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = InteractionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<ReportResponse>> createReport(
            @Valid @RequestBody CreateReportRequest request) {
        log.debug("ReportController_createReport_Creating new report for target: {} {}", request.getTargetType(), request.getTargetId());
        ReportResponse response = reportService.createReport(request);
        return success(response, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all reports",
            description = "Get list of all reports with pagination (Admin/Staff only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_REPORT_RETRIEVED_ALL_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = InteractionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ReportResponse>>> getReports(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {
        log.debug("ReportController_getReports_Fetching reports with page: {}, size: {}", page, size);
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        return success(reportService.getReports(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/{reportId}")
    @Operation(summary = "Get report by ID",
            description = "Get report information by report ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_REPORT_RETRIEVED_BY_ID_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = InteractionMessageConstants.REPORT_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<ReportResponse>> getReportById(@PathVariable String reportId) {
        log.debug("ReportController_getReportById_Fetching report with id: {}", reportId);
        return success(reportService.getById(reportId), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/target/{targetType}/{targetId}")
    @Operation(summary = "Get reports by target",
            description = "Get list of reports for a specific target with pagination (Admin/Staff only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_REPORT_RETRIEVED_BY_TARGET_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = InteractionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ReportResponse>>> getReportsByTarget(
            @Parameter(description = "Target type", required = true)
            @PathVariable String targetType,
            @Parameter(description = "Target ID", required = true)
            @PathVariable String targetId,
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {
        log.debug("ReportController_getReportsByTarget_Fetching reports for target: {} {}", targetType, targetId);
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        return success(reportService.getReportsByTarget(targetType, targetId, request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get reports by status",
            description = "Get list of reports by status with pagination (Admin/Staff only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_REPORT_RETRIEVED_BY_STATUS_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = InteractionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ReportResponse>>> getReportsByStatus(
            @Parameter(description = "Report status", required = true)
            @PathVariable ReportStatus status,
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {
        log.debug("ReportController_getReportsByStatus_Fetching reports with status: {}", status);
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        return success(reportService.getReportsByStatus(status, request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/my-reports")
    @Operation(summary = "Get my reports",
            description = "Get list of reports created by current user with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_REPORT_RETRIEVED_MY_REPORTS_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = InteractionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ReportResponse>>> getMyReports(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {
        log.debug("ReportController_getMyReports_Fetching my reports");
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        return success(reportService.getMyReports(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @PutMapping("/{reportId}/status")
    @Operation(summary = "Update report status",
            description = "Update report status (Admin/Staff only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_REPORT_UPDATED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = InteractionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = InteractionMessageConstants.REPORT_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<ReportResponse>> updateReportStatus(
            @PathVariable String reportId,
            @Valid @RequestBody UpdateReportRequest request) {
        log.debug("ReportController_updateReportStatus_Updating report status for id: {} to {}", reportId, request.getStatus());
        return success(reportService.updateReportStatus(reportId, request), MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @DeleteMapping("/{reportId}")
    @Operation(summary = "Delete report",
            description = "Delete a report (Admin or reporter only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_REPORT_DELETED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = InteractionMessageConstants.REPORT_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<Void>> deleteReport(@PathVariable String reportId) {
        log.debug("ReportController_deleteReport_Deleting report with id: {}", reportId);
        reportService.deleteReport(reportId);
        return success(null, MessageCodeConstant.M004_DELETED, MessageConstant.DELETED);
    }

    @GetMapping("/check/{targetType}/{targetId}")
    @Operation(summary = "Check if user reported target",
            description = "Check if current user has reported a specific target")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_REPORT_CHECK_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = InteractionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<Map<String, Object>>> checkIfReported(
            @Parameter(description = "Target type", required = true)
            @PathVariable String targetType,
            @Parameter(description = "Target ID", required = true)
            @PathVariable String targetId) {
        log.debug("ReportController_checkIfReported_Checking if user reported target: {} {}", targetType, targetId);
        boolean hasReported = reportService.hasUserReported(targetType, targetId);
        Map<String, Object> data = new HashMap<>();
        data.put("hasReported", hasReported);
        return success(data, MessageCodeConstant.M005_RETRIEVED, InteractionMessageConstants.API_REPORT_CHECK_SUCCESS);
    }

    @GetMapping("/summary")
    @Operation(summary = "Get report summary",
            description = "Get report summary for dashboard (Admin/Staff only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_REPORT_SUMMARY_RETRIEVED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<ReportSummaryResponse>> getReportSummary() {
        log.debug("ReportController_getReportSummary_Fetching report summary");
        return success(reportService.getReportSummary(), MessageCodeConstant.M005_RETRIEVED, InteractionMessageConstants.API_REPORT_SUMMARY_RETRIEVED_SUCCESS);
    }
}

