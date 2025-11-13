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
import com.se.hub.modules.exam.entity.Question;
import com.se.hub.modules.exam.mapper.ExamMapper;
import com.se.hub.modules.exam.repository.ExamRepository;
import com.se.hub.modules.exam.repository.QuestionRepository;
import com.se.hub.modules.exam.service.api.ExamService;
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
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamServiceImpl implements ExamService {
    ExamRepository examRepository;
    QuestionRepository questionRepository;
    CourseRepository courseRepository;
    ExamMapper examMapper;

    @Override
    @Transactional
    public ExamResponse create(CreateExamRequest request) {
        if (examRepository.existsByExamCode(request.getExamCode())) {
            throw new AppException(ErrorCode.DATA_EXISTED);
        }

        Exam exam = examMapper.toExam(request);
        if (request.getCourseId() != null) {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
            exam.setCourse(course);
        }

        String userId = AuthUtils.getCurrentUserId();
        exam.setCreatedBy(userId);
        exam.setUpdateBy(userId);

        exam.setQuestions(new HashSet<>());

        return examMapper.toExamResponse(examRepository.save(exam));
    }

    @Override
    public ExamResponse getById(String examId) {
        return examMapper.toExamResponse(examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND)));
    }

    @Override
    public PagingResponse<ExamResponse> getAll(PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Exam> examPages = examRepository.findAll(pageable);

        return PagingResponse.<ExamResponse>builder()
                .currentPage(request.getPage())
                .pageSize(examPages.getSize())
                .totalPages(examPages.getTotalPages())
                .totalElement(examPages.getTotalElements())
                .data(examPages.getContent().stream().map(examMapper::toExamResponse).toList())
                .build();
    }

    @Override
    public PagingResponse<ExamResponse> getByCourseId(String courseId, PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Exam> examPages = examRepository.findAllByCourse_Id(courseId, pageable);

        return PagingResponse.<ExamResponse>builder()
                .currentPage(request.getPage())
                .pageSize(examPages.getSize())
                .totalPages(examPages.getTotalPages())
                .totalElement(examPages.getTotalElements())
                .data(examPages.getContent().stream().map(examMapper::toExamResponse).toList())
                .build();
    }

    @Override
    @Transactional
    public ExamResponse updateById(String examId, UpdateExamRequest request) {
        Exam exam = examRepository.findById(examId).orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        if (request.getCourseId() != null) {
            Course course = courseRepository.findById(request.getCourseId()).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
            exam.setCourse(course);
        }

        examMapper.updateExamFromRequest(exam, request);
        exam.setUpdateBy(AuthUtils.getCurrentUserId());

        return examMapper.toExamResponse(examRepository.save(exam));
    }

    @Override
    @Transactional
    public void deleteById(String examId) {
        examRepository.deleteById(examId);
    }

    @Override
    @Transactional
    public ExamResponse addQuestions(String examId, AddQuestionsToExamRequest request) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
        Set<Question> current = exam.getQuestions() == null ? new HashSet<>() : exam.getQuestions();

        List<Question> toAdd = questionRepository.findAllById(request.getQuestionIds());
        if (toAdd.size() != request.getQuestionIds().size()) {
            throw new AppException(ErrorCode.DATA_INVALID);
        }
        current.addAll(toAdd);
        exam.setQuestions(current);
        exam.setUpdateBy(AuthUtils.getCurrentUserId());
        return examMapper.toExamResponse(examRepository.save(exam));
    }

    @Override
    @Transactional
    public ExamResponse removeQuestions(String examId, RemoveQuestionsFromExamRequest request) {
        Exam exam = examRepository.findById(examId).
                orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
        if (exam.getQuestions() == null || exam.getQuestions().isEmpty()) {
            return examMapper.toExamResponse(exam);
        }
        Set<String> toRemove = new HashSet<>(request.getQuestionIds());
        Set<Question> remaining = new HashSet<>();
        for (Question q : exam.getQuestions()) {
            if (!toRemove.contains(q.getId())) {
                remaining.add(q);
            }
        }
        exam.setQuestions(remaining);
        exam.setUpdateBy(AuthUtils.getCurrentUserId());
        return examMapper.toExamResponse(examRepository.save(exam));
    }
}


