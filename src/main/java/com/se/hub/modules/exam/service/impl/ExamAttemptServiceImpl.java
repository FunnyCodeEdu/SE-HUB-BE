package com.se.hub.modules.exam.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.exam.dto.request.SubmitExamRequest;
import com.se.hub.modules.exam.dto.response.ExamResultResponse;
import com.se.hub.modules.exam.entity.Exam;
import com.se.hub.modules.exam.entity.ExamAttempt;
import com.se.hub.modules.exam.entity.Question;
import com.se.hub.modules.exam.entity.QuestionOption;
import com.se.hub.modules.exam.mapper.ExamAttemptMapper;
import com.se.hub.modules.exam.repository.ExamAttemptRepository;
import com.se.hub.modules.exam.repository.ExamRepository;
import com.se.hub.modules.exam.service.api.ExamAttemptService;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.profile.repository.ProfileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamAttemptServiceImpl implements ExamAttemptService {
    
    ExamAttemptRepository examAttemptRepository;
    ExamRepository examRepository;
    ProfileRepository profileRepository;
    ExamAttemptMapper examAttemptMapper;
    ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    @Transactional
    public ExamResultResponse submitExam(SubmitExamRequest request) {
        log.info("ExamAttemptServiceImpl_submitExam_Submitting exam with examId: {}", request.getExamId());
        
        // 1. Validate and get exam
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> {
                    log.error("ExamAttemptServiceImpl_submitExam_Exam not found: {}", request.getExamId());
                    return new AppException(ErrorCode.EXAM_NOT_FOUND);
                });
        
        // 2. Get current user's profile
        String userId = AuthUtils.getCurrentUserId();
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("ExamAttemptServiceImpl_submitExam_Profile not found for userId: {}", userId);
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                });
        
        // 3. Get all questions for the exam
        List<Question> questions = new ArrayList<>(exam.getQuestions());
        if (questions.isEmpty()) {
            log.error("ExamAttemptServiceImpl_submitExam_Exam has no questions: {}", request.getExamId());
            throw new AppException(ErrorCode.DATA_INVALID);
        }
        
        // 4. Calculate score
        int totalScore = 0;
        int earnedScore = 0;
        int correctCount = 0;
        List<ExamResultResponse.QuestionResultDetail> questionResults = new ArrayList<>();
        
        for (Question question : questions) {
            totalScore += question.getScore();
            String selectedOptionId = request.getAnswers().get(question.getId());
            ExamResultResponse.QuestionResultDetail detail = calculateQuestionScore(
                    question, 
                    selectedOptionId
            );
            
            questionResults.add(detail);
            earnedScore += detail.getPointsEarned();
            if (detail.isCorrect()) {
                correctCount++;
            }
        }
        
        // 5. Convert answers to JSON
        String submittedAnswersJson;
        try {
            submittedAnswersJson = objectMapper.writeValueAsString(request.getAnswers());
        } catch (JsonProcessingException e) {
            log.error("ExamAttemptServiceImpl_submitExam_Error converting answers to JSON", e);
            throw new AppException(ErrorCode.DATA_INVALID);
        }
        
        // 6. Save exam attempt
        ExamAttempt attempt = ExamAttempt.builder()
                .exam(exam)
                .profile(profile)
                .score(earnedScore)
                .totalScore(totalScore)
                .correctCount(correctCount)
                .totalQuestions(questions.size())
                .timeTakenSeconds(request.getTimeTakenSeconds())
                .submittedAnswers(submittedAnswersJson)
                .build();
        
        attempt.setCreatedBy(userId);
        attempt.setUpdateBy(userId);
        
        ExamAttempt savedAttempt = examAttemptRepository.save(attempt);
        log.info("ExamAttemptServiceImpl_submitExam_Saved exam attempt: {}", savedAttempt.getId());
        
        // 7. Build result response
        ExamResultResponse response = examAttemptMapper.toExamResultResponse(savedAttempt);
        response.setPercentage(calculatePercentage(earnedScore, totalScore));
        response.setQuestionResults(questionResults);
        
        return response;
    }
    
    @Override
    public PagingResponse<ExamResultResponse> getAttemptHistory(String profileId, PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );
        
        Page<ExamAttempt> attemptPages = examAttemptRepository.findByProfileIdOrderByCreateDateDesc(profileId, pageable);
        
        return PagingResponse.<ExamResultResponse>builder()
                .currentPage(request.getPage())
                .pageSize(attemptPages.getSize())
                .totalPages(attemptPages.getTotalPages())
                .totalElement(attemptPages.getTotalElements())
                .data(attemptPages.getContent().stream()
                        .map(this::toExamResultResponseSimple)
                        .toList())
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PagingResponse<ExamResultResponse> getAttemptHistoryByExam(String examId, PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );
        
        Page<ExamAttempt> attemptPages = examAttemptRepository.findByExamIdOrderByCreateDateDesc(examId, pageable);
        
        return PagingResponse.<ExamResultResponse>builder()
                .currentPage(request.getPage())
                .pageSize(attemptPages.getSize())
                .totalPages(attemptPages.getTotalPages())
                .totalElement(attemptPages.getTotalElements())
                .data(attemptPages.getContent().stream()
                        .map(this::toExamResultResponseSimple)
                        .toList())
                .build();
    }
    
    @Override
    public ExamResultResponse getAttemptById(String attemptId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> {
                    log.error("ExamAttemptServiceImpl_getAttemptById_Attempt not found: {}", attemptId);
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                });
        
        return toExamResultResponseWithDetails(attempt);
    }
    
    @Override
    public PagingResponse<ExamResultResponse> getAllAttempts(PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );
        
        Page<ExamAttempt> attemptPages = examAttemptRepository.findAll(pageable);
        
        return PagingResponse.<ExamResultResponse>builder()
                .currentPage(request.getPage())
                .pageSize(attemptPages.getSize())
                .totalPages(attemptPages.getTotalPages())
                .totalElement(attemptPages.getTotalElements())
                .data(attemptPages.getContent().stream()
                        .map(this::toExamResultResponseSimple)
                        .toList())
                .build();
    }
    
    /**
     * Calculate score for a single question
     */
    private ExamResultResponse.QuestionResultDetail calculateQuestionScore(
            Question question, 
            String selectedOptionId
    ) {
        String correctOptionId = findCorrectOptionId(question);
        boolean isCorrect = correctOptionId != null && correctOptionId.equals(selectedOptionId);
        
        int pointsEarned = isCorrect ? question.getScore() : 0;
        
        return ExamResultResponse.QuestionResultDetail.builder()
                .questionId(question.getId())
                .questionContent(question.getContent())
                .selectedOptionId(selectedOptionId)
                .correctOptionId(correctOptionId)
                .isCorrect(isCorrect)
                .pointsEarned(pointsEarned)
                .pointsTotal(question.getScore())
                .build();
    }
    
    /**
     * Find the correct option ID for a question
     */
    private String findCorrectOptionId(Question question) {
        if (question.getOptions() == null) {
            return null;
        }
        
        return question.getOptions().stream()
                .filter(QuestionOption::getIsCorrect)
                .findFirst()
                .map(QuestionOption::getId)
                .orElse(null);
    }
    
    /**
     * Calculate percentage score
     */
    private double calculatePercentage(int earned, int total) {
        if (total == 0) {
            return 0.0;
        }
        return (double) earned / total * 100;
    }
    
    /**
     * Build exam result response with question details
     */
    private ExamResultResponse toExamResultResponseWithDetails(ExamAttempt attempt) {
        ExamResultResponse response = examAttemptMapper.toExamResultResponse(attempt);
        response.setPercentage(calculatePercentage(attempt.getScore(), attempt.getTotalScore()));
        
        // Load question details if needed
        List<ExamResultResponse.QuestionResultDetail> details = new ArrayList<>();
        if (attempt.getSubmittedAnswers() != null) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, String> answers = objectMapper.readValue(
                        attempt.getSubmittedAnswers(), 
                        Map.class
                );
                
                Exam exam = attempt.getExam();
                for (Question question : exam.getQuestions()) {
                    String selectedOptionId = answers.get(question.getId());
                    ExamResultResponse.QuestionResultDetail detail = calculateQuestionScore(
                            question, 
                            selectedOptionId
                    );
                    details.add(detail);
                }
            } catch (JsonProcessingException e) {
                log.error("ExamAttemptServiceImpl_toExamResultResponseWithDetails_Error parsing JSON", e);
            }
        }
        
        response.setQuestionResults(details);
        
        // Set profile information
        if (attempt.getProfile() != null) {
            ExamResultResponse.ProfileInfo profileInfo = ExamResultResponse.ProfileInfo.builder()
                    .id(attempt.getProfile().getId())
                    .displayName(attempt.getProfile().getFullName())
                    .username(attempt.getProfile().getUser() != null ? attempt.getProfile().getUser().getUsername() : null)
                    .avtUrl(attempt.getProfile().getAvtUrl())
                    .build();
            response.setProfile(profileInfo);
        }
        
        return response;
    }
    
    /**
     * Build simple exam result response without question details (for list views)
     */
    private ExamResultResponse toExamResultResponseSimple(ExamAttempt attempt) {
        ExamResultResponse response = examAttemptMapper.toExamResultResponse(attempt);
        response.setPercentage(calculatePercentage(attempt.getScore(), attempt.getTotalScore()));
        
        // Set profile information
        if (attempt.getProfile() != null) {
            ExamResultResponse.ProfileInfo profileInfo = ExamResultResponse.ProfileInfo.builder()
                    .id(attempt.getProfile().getId())
                    .displayName(attempt.getProfile().getFullName())
                    .username(attempt.getProfile().getUser() != null ? attempt.getProfile().getUser().getUsername() : null)
                    .avtUrl(attempt.getProfile().getAvtUrl())
                    .build();
            response.setProfile(profileInfo);
        }
        
        return response;
    }
}

