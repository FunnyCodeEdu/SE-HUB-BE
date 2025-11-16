package com.se.hub.modules.exam.controller;

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
import com.se.hub.modules.exam.constant.AnswerReportMessageConstants;
import com.se.hub.modules.exam.dto.request.CreateAnswerReportRequest;
import com.se.hub.modules.exam.dto.request.UpdateAnswerReportStatusRequest;
import com.se.hub.modules.exam.dto.response.AnswerReportResponse;
import com.se.hub.modules.exam.service.AnswerReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Answer Report Management",
        description = "Answer report management API for reporting incorrect answers")
@RequestMapping("/answer-reports")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class AnswerReportController extends BaseController {
    AnswerReportService answerReportService;

    @PostMapping
    @Operation(summary = "Create new answer report",
            description = "Create a new report for an incorrect answer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = AnswerReportMessageConstants.API_ANSWER_REPORT_CREATED_SUCCESS,
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = AnswerReportMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = AnswerReportMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<AnswerReportResponse>> createAnswerReport(
            @Valid @RequestBody CreateAnswerReportRequest request) {
        AnswerReportResponse response = answerReportService.createAnswerReport(request);
        return success(response, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }

    @GetMapping("/admin")
    @Operation(summary = "Get all answer reports (Admin only)",
            description = "Get list of all answer reports with pagination (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = AnswerReportMessageConstants.API_ANSWER_REPORT_RETRIEVED_ALL_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = AnswerReportMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.FORBIDDEN_403, description = "Forbidden - Admin access required"),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = AnswerReportMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<AnswerReportResponse>>> getAllAnswerReports(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        return success(answerReportService.getAllAnswerReports(request),
                MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @PutMapping("/{reportId}/status")
    @Operation(summary = "Update answer report status (Admin only)",
            description = "Update answer report status to APPROVED (treated as UPDATED) or REJECTED (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = "Answer report status updated successfully",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = AnswerReportMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = AnswerReportMessageConstants.ANSWER_REPORT_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.FORBIDDEN_403, description = "Forbidden - Admin access required"),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = AnswerReportMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<AnswerReportResponse>> updateAnswerReportStatus(
            @PathVariable String reportId,
            @Valid @RequestBody UpdateAnswerReportStatusRequest request) {
        AnswerReportResponse response = answerReportService.updateAnswerReportStatus(reportId, request.getStatus());
        return success(response, MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }
}

