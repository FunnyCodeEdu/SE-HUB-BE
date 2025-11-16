package com.se.hub.modules.exam.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.exam.dto.request.CreateAnswerReportRequest;
import com.se.hub.modules.exam.dto.response.AnswerReportResponse;
import com.se.hub.modules.exam.entity.AnswerReport;
import com.se.hub.modules.exam.entity.Question;
import com.se.hub.modules.exam.entity.QuestionOption;
import com.se.hub.modules.exam.enums.AnswerReportStatus;
import com.se.hub.modules.exam.exception.AnswerReportErrorCode;
import com.se.hub.modules.exam.mapper.AnswerReportMapper;
import com.se.hub.modules.exam.repository.AnswerReportRepository;
import com.se.hub.modules.exam.repository.QuestionOptionRepository;
import com.se.hub.modules.exam.repository.QuestionRepository;
import com.se.hub.modules.exam.service.AnswerReportService;
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

/**
 * Answer Report Service Implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerReportServiceImpl implements AnswerReportService {
    AnswerReportRepository answerReportRepository;
    QuestionRepository questionRepository;
    QuestionOptionRepository questionOptionRepository;
    AnswerReportMapper answerReportMapper;

    @Override
    @Transactional
    public AnswerReportResponse createAnswerReport(CreateAnswerReportRequest request) {
        log.debug("AnswerReportService_createAnswerReport_Creating new answer report for user: {}", AuthUtils.getCurrentUserId());

        // Validate question exists
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> {
                    log.error("AnswerReportService_createAnswerReport_Question not found with id: {}", request.getQuestionId());
                    return AnswerReportErrorCode.ANSWER_REPORT_QUESTION_ID_INVALID.toException();
                });

        // Validate question option exists and belongs to the question
        QuestionOption questionOption = questionOptionRepository.findById(request.getQuestionOptionId())
                .orElseThrow(() -> {
                    log.error("AnswerReportService_createAnswerReport_Question option not found with id: {}", request.getQuestionOptionId());
                    return AnswerReportErrorCode.ANSWER_REPORT_QUESTION_OPTION_ID_INVALID.toException();
                });

        if (!questionOption.getQuestion().getId().equals(question.getId())) {
            log.error("AnswerReportService_createAnswerReport_Question option does not belong to the question");
            throw AnswerReportErrorCode.ANSWER_REPORT_QUESTION_OPTION_ID_INVALID.toException();
        }

        // Create answer report
        AnswerReport answerReport = answerReportMapper.toAnswerReport(request);
        answerReport.setQuestion(question);
        answerReport.setQuestionOption(questionOption);
        answerReport.setReporterId(AuthUtils.getCurrentUserId());
        answerReport.setStatus(AnswerReportStatus.PENDING);

        String userId = AuthUtils.getCurrentUserId();
        answerReport.setCreatedBy(userId);
        answerReport.setUpdateBy(userId);

        AnswerReport savedReport = answerReportRepository.save(answerReport);
        log.debug("AnswerReportService_createAnswerReport_Answer report created successfully with id: {}", savedReport.getId());

        return answerReportMapper.toAnswerReportResponse(savedReport);
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<AnswerReportResponse> getAllAnswerReports(PagingRequest request) {
        log.debug("AnswerReportService_getAllAnswerReports_Fetching all answer reports with page: {}, size: {}",
                request.getPage(), request.getPageSize());

        checkAdminPermission();

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<AnswerReport> reports = answerReportRepository.findAll(pageable);
        return buildPagingResponse(reports);
    }

    @Override
    @Transactional
    public AnswerReportResponse updateAnswerReportStatus(String reportId, AnswerReportStatus status) {
        log.debug("AnswerReportService_updateAnswerReportStatus_Updating answer report status with id: {}, new status: {}", reportId, status);

        checkAdminPermission();

        // Validate status - only allow APPROVED (treated as UPDATED) or REJECTED
        if (status != AnswerReportStatus.APPROVED && status != AnswerReportStatus.REJECTED) {
            log.error("AnswerReportService_updateAnswerReportStatus_Invalid status: {}. Only APPROVED or REJECTED allowed", status);
            throw AnswerReportErrorCode.ANSWER_REPORT_ALREADY_PROCESSED.toException();
        }

        AnswerReport report = answerReportRepository.findById(reportId)
                .orElseThrow(() -> {
                    log.error("AnswerReportService_updateAnswerReportStatus_Answer report not found with id: {}", reportId);
                    return AnswerReportErrorCode.ANSWER_REPORT_NOT_FOUND.toException();
                });

        // Allow update to any status (APPROVED or REJECTED) from any status
        // This provides flexibility to switch between statuses
        report.setStatus(status);
        report.setAdminId(AuthUtils.getCurrentUserId());
        report.setUpdateBy(AuthUtils.getCurrentUserId());

        AnswerReport savedReport = answerReportRepository.save(report);
        log.debug("AnswerReportService_updateAnswerReportStatus_Answer report status updated successfully with id: {}, new status: {}", reportId, status);

        return answerReportMapper.toAnswerReportResponse(savedReport);
    }

    /**
     * Helper method to build PagingResponse from Page<AnswerReport>
     */
    private PagingResponse<AnswerReportResponse> buildPagingResponse(Page<AnswerReport> reports) {
        return PagingResponse.<AnswerReportResponse>builder()
                .currentPage(reports.getNumber() + GlobalVariable.PAGE_SIZE_INDEX)
                .totalPages(reports.getTotalPages())
                .pageSize(reports.getSize())
                .totalElement(reports.getTotalElements())
                .data(reports.getContent().stream()
                        .map(answerReportMapper::toAnswerReportResponse)
                        .toList())
                .build();
    }

    /**
     * Check if current user is admin
     */
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
    }

    /**
     * Check admin permission and throw exception if not authorized
     */
    private void checkAdminPermission() {
        if (!isAdmin()) {
            log.error("AnswerReportService_checkAdminPermission_User {} is not admin", AuthUtils.getCurrentUserId());
            throw AnswerReportErrorCode.ANSWER_REPORT_FORBIDDEN_OPERATION.toException();
        }
    }
}

