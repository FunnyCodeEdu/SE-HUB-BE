package com.se.hub.modules.exam.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.course.entity.Course;
import com.se.hub.modules.course.repository.CourseRepository;
import com.se.hub.modules.exam.dto.request.AddQuestionsToExamRequest;
import com.se.hub.modules.exam.dto.request.CreateExamRequest;
import com.se.hub.modules.exam.dto.request.RemoveQuestionsFromExamRequest;
import com.se.hub.modules.exam.dto.request.UpdateExamRequest;
import com.se.hub.modules.exam.dto.response.ExamResponse;
import com.se.hub.modules.exam.entity.Exam;
import com.se.hub.modules.interaction.dto.response.ReactionInfo;
import com.se.hub.modules.interaction.enums.TargetType;
import com.se.hub.modules.interaction.service.api.ReactionService;
import com.se.hub.modules.exam.entity.Question;
import com.se.hub.modules.exam.exception.ExamErrorCode;
import com.se.hub.modules.exam.mapper.ExamMapper;
import com.se.hub.modules.exam.repository.ExamRepository;
import com.se.hub.modules.exam.repository.QuestionRepository;
import com.se.hub.modules.exam.service.ExamService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Exam Service Implementation
 * 
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
public class ExamServiceImpl implements ExamService {
    ExamRepository examRepository;
    QuestionRepository questionRepository;
    CourseRepository courseRepository;
    ExamMapper examMapper;
    ReactionService reactionService;

    /**
     * Helper method to build PagingResponse from Page<Exam>
     * Reduces code duplication across get methods
     */
    private PagingResponse<ExamResponse> buildPagingResponse(Page<Exam> exams) {
        List<Exam> examList = exams.getContent();
        String currentUserId = AuthUtils.getCurrentUserId();
        
        // Batch check reactions for all exams
        List<String> examIds = examList.stream().map(Exam::getId).toList();
        Map<String, ReactionInfo> reactionsMap = reactionService
                .getReactionsForTargets(TargetType.EXAM, examIds, currentUserId);
        
        return PagingResponse.<ExamResponse>builder()
                .currentPage(exams.getNumber() + GlobalVariable.PAGE_SIZE_INDEX)
                .totalPages(exams.getTotalPages())
                .pageSize(exams.getSize())
                .totalElement(exams.getTotalElements())
                .data(examList.stream()
                        .map(exam -> {
                            ExamResponse response = examMapper.toExamResponse(exam);
                            ReactionInfo reactionInfo = reactionsMap.getOrDefault(
                                    exam.getId(),
                                    ReactionInfo.builder().userReacted(false).type(null).build()
                            );
                            response.setReactions(reactionInfo);
                            return response;
                        })
                        .toList()
                )
                .build();
    }

    @Override
    @Transactional
    public ExamResponse create(CreateExamRequest request) {
        log.debug("ExamService_create_Creating new exam for user: {}", AuthUtils.getCurrentUserId());
        
        if (examRepository.existsByExamCode(request.getExamCode())) {
            log.error("ExamService_create_Exam code already exists: {}", request.getExamCode());
            throw ExamErrorCode.EXAM_CODE_EXISTED.toException();
        }

        Exam exam = examMapper.toExam(request);
        if (request.getCourseId() != null) {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> {
                        log.error("ExamService_create_Course not found: {}", request.getCourseId());
                        return new AppException(ErrorCode.COURSE_NOT_FOUND);
                    });
            exam.setCourse(course);
        }

        String userId = AuthUtils.getCurrentUserId();
        exam.setCreatedBy(userId);
        exam.setUpdateBy(userId);

        exam.setQuestions(new HashSet<>());

        ExamResponse response = examMapper.toExamResponse(examRepository.save(exam));
        log.debug("ExamService_create_Exam created successfully with id: {}", response.getId());
        return response;
    }

    @Override
    public ExamResponse getById(String examId) {
        log.debug("ExamService_getById_Fetching exam with id: {}", examId);
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> {
                    log.error("ExamService_getById_Exam not found with id: {}", examId);
                    return ExamErrorCode.EXAM_NOT_FOUND.toException();
                });
        
        ExamResponse response = examMapper.toExamResponse(exam);
        String currentUserId = AuthUtils.getCurrentUserId();
        Map<String, ReactionInfo> reactionsMap = reactionService
                .getReactionsForTargets(TargetType.EXAM, List.of(examId), currentUserId);
        ReactionInfo reactionInfo = reactionsMap.getOrDefault(
                examId,
                ReactionInfo.builder().userReacted(false).type(null).build()
        );
        response.setReactions(reactionInfo);
        return response;
    }

    @Override
    public PagingResponse<ExamResponse> getAll(PagingRequest request) {
        log.debug("ExamService_getAll_Fetching exams with page: {}, size: {}", 
                request.getPage(), request.getPageSize());
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Exam> examPages = examRepository.findAll(pageable);
        return buildPagingResponse(examPages);
    }

    @Override
    public PagingResponse<ExamResponse> getByCourseId(String courseId, PagingRequest request) {
        log.debug("ExamService_getByCourseId_Fetching exams for course: {} with page: {}, size: {}", 
                courseId, request.getPage(), request.getPageSize());
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Exam> examPages = examRepository.findAllByCourse_Id(courseId, pageable);
        return buildPagingResponse(examPages);
    }

    @Override
    @Transactional
    public ExamResponse updateById(String examId, UpdateExamRequest request) {
        log.debug("ExamService_updateById_Updating exam with id: {}", examId);
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> {
                    log.error("ExamService_updateById_Exam not found with id: {}", examId);
                    return ExamErrorCode.EXAM_NOT_FOUND.toException();
                });

        if (request.getCourseId() != null) {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> {
                        log.error("ExamService_updateById_Course not found: {}", request.getCourseId());
                        return new AppException(ErrorCode.COURSE_NOT_FOUND);
                    });
            exam.setCourse(course);
        }

        examMapper.updateExamFromRequest(exam, request);
        exam.setUpdateBy(AuthUtils.getCurrentUserId());

        ExamResponse response = examMapper.toExamResponse(examRepository.save(exam));
        log.debug("ExamService_updateById_Exam updated successfully with id: {}", examId);
        return response;
    }

    @Override
    @Transactional
    public void deleteById(String examId) {
        log.debug("ExamService_deleteById_Deleting exam with id: {}", examId);
        if (examId == null || examId.isBlank()) {
            log.error("ExamService_deleteById_Exam ID is required");
            throw ExamErrorCode.EXAM_ID_REQUIRED.toException();
        }

        if (!examRepository.existsById(examId)) {
            log.error("ExamService_deleteById_Exam not found with id: {}", examId);
            throw ExamErrorCode.EXAM_NOT_FOUND.toException();
        }

        examRepository.deleteById(examId);
        log.debug("ExamService_deleteById_Exam deleted successfully with id: {}", examId);
    }

    @Override
    @Transactional
    public ExamResponse addQuestions(String examId, AddQuestionsToExamRequest request) {
        log.debug("ExamService_addQuestions_Adding questions to exam: {}", examId);
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> {
                    log.error("ExamService_addQuestions_Exam not found with id: {}", examId);
                    return ExamErrorCode.EXAM_NOT_FOUND.toException();
                });
        Set<Question> current = exam.getQuestions() == null ? new HashSet<>() : exam.getQuestions();

        List<Question> toAdd = questionRepository.findAllById(request.getQuestionIds());
        if (toAdd.size() != request.getQuestionIds().size()) {
            log.error("ExamService_addQuestions_Some questions not found. Expected: {}, Found: {}", 
                    request.getQuestionIds().size(), toAdd.size());
            throw ExamErrorCode.EXAM_QUESTIONS_INVALID.toException();
        }
        current.addAll(toAdd);
        exam.setQuestions(current);
        exam.setUpdateBy(AuthUtils.getCurrentUserId());
        
        ExamResponse response = examMapper.toExamResponse(examRepository.save(exam));
        log.debug("ExamService_addQuestions_Questions added successfully to exam: {}", examId);
        return response;
    }

    @Override
    @Transactional
    public ExamResponse removeQuestions(String examId, RemoveQuestionsFromExamRequest request) {
        log.debug("ExamService_removeQuestions_Removing questions from exam: {}", examId);
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> {
                    log.error("ExamService_removeQuestions_Exam not found with id: {}", examId);
                    return ExamErrorCode.EXAM_NOT_FOUND.toException();
                });
        
        if (exam.getQuestions() == null || exam.getQuestions().isEmpty()) {
            log.debug("ExamService_removeQuestions_Exam has no questions to remove");
            return examMapper.toExamResponse(exam);
        }
        
        Set<String> toRemove = new HashSet<>(request.getQuestionIds());
        Set<Question> remaining = exam.getQuestions().stream()
                .filter(q -> !toRemove.contains(q.getId()))
                .collect(java.util.stream.Collectors.toSet());
        
        exam.setQuestions(remaining);
        exam.setUpdateBy(AuthUtils.getCurrentUserId());
        
        ExamResponse response = examMapper.toExamResponse(examRepository.save(exam));
        log.debug("ExamService_removeQuestions_Questions removed successfully from exam: {}", examId);
        return response;
    }
}


