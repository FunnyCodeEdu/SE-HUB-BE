package com.se.hub.modules.exam.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.exam.dto.request.CreateQuestionRequest;
import com.se.hub.modules.exam.dto.request.CreateQuestionOptionRequest;
import com.se.hub.modules.exam.dto.request.UpdateQuestionOptionRequest;
import com.se.hub.modules.exam.dto.request.UpdateQuestionRequest;
import com.se.hub.modules.exam.dto.response.QuestionResponse;
import com.se.hub.modules.exam.entity.AnswerReport;
import com.se.hub.modules.exam.entity.Question;
import com.se.hub.modules.exam.entity.QuestionOption;
import com.se.hub.modules.exam.enums.QuestionCategory;
import com.se.hub.modules.exam.enums.QuestionDifficulty;
import com.se.hub.modules.exam.enums.QuestionType;
import com.se.hub.modules.exam.exception.QuestionErrorCode;
import com.se.hub.modules.exam.mapper.QuestionMapper;
import com.se.hub.modules.exam.mapper.QuestionOptionMapper;
import com.se.hub.modules.exam.repository.AnswerReportRepository;
import com.se.hub.modules.exam.repository.QuestionOptionRepository;
import com.se.hub.modules.exam.repository.QuestionRepository;
import com.se.hub.modules.exam.service.QuestionService;
import com.se.hub.modules.exam.utils.QuestionHashUtil;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Question Service Implementation
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
public class QuestionServiceImpl implements QuestionService {
    QuestionRepository questionRepository;
    QuestionOptionRepository questionOptionRepository;
    AnswerReportRepository answerReportRepository;
    QuestionMapper questionMapper;
    QuestionOptionMapper questionOptionMapper;

    /**
     * Helper method to build PagingResponse from Page<Question>
     * Reduces code duplication across get methods
     */
    private PagingResponse<QuestionResponse> buildPagingResponse(Page<Question> questions) {
        return PagingResponse.<QuestionResponse>builder()
                .currentPage(questions.getNumber() + GlobalVariable.PAGE_SIZE_INDEX)
                .totalPages(questions.getTotalPages())
                .pageSize(questions.getSize())
                .totalElement(questions.getTotalElements())
                .data(questions.getContent().stream()
                        .map(questionMapper::toQuestionResponse)
                        .toList()
                )
                .build();
    }

    @Override
    @Transactional
    public QuestionResponse createQuestion(CreateQuestionRequest request) {
        log.debug("QuestionService_createQuestion_Creating new question for user: {}", AuthUtils.getCurrentUserId());
        
        // Validate options based on question type
        // CONTENT type questions don't require options, other types do
        if (request.getQuestionType() != QuestionType.CONTENT) {
            if (request.getOptions() == null || request.getOptions().isEmpty()) {
                log.error("QuestionService_createQuestion_Options are required for question type: {}", request.getQuestionType());
                throw QuestionErrorCode.QUESTION_TYPE_INVALID.toException();
            }
        }
        
        // Extract option contents for hash generation
        List<String> optionContents = request.getOptions() != null 
                ? request.getOptions().stream()
                        .map(CreateQuestionOptionRequest::getContent)
                        .toList()
                : new ArrayList<>();
        
        // Generate hash from question content and options
        String contentHash = QuestionHashUtil.generateQuestionHash(request.getContent(), optionContents);
        String normalizedText = QuestionHashUtil.buildHashContent(request.getContent(), optionContents);
        
        Question question = questionMapper.toQuestion(request);
        question.setContentHash(contentHash);
        question.setNormalizedText(normalizedText);
        String userId = AuthUtils.getCurrentUserId();
        question.setCreatedBy(userId);
        question.setUpdateBy(userId);

        // Save question first
        Question savedQuestion = questionRepository.save(question);

        // Create and save question options if provided
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            savedQuestion.setOptions(createOptions(userId, savedQuestion, request.getOptions()));
        }

        QuestionResponse response = questionMapper.toQuestionResponse(savedQuestion);
        log.debug("QuestionService_createQuestion_Question created successfully with id: {}", response.getId());
        return response;
    }

    @Override
    public QuestionResponse getById(String questionId) {
        log.debug("QuestionService_getById_Fetching question with id: {}", questionId);
        return questionMapper.toQuestionResponse(questionRepository.findById(questionId)
                .orElseThrow(() -> {
                    log.error("QuestionService_getById_Question not found with id: {}", questionId);
                    return QuestionErrorCode.QUESTION_NOT_FOUND.toException();
                }));
    }

    @Override
    public PagingResponse<QuestionResponse> getAllQuestions(PagingRequest request) {
        log.debug("QuestionService_getAllQuestions_Fetching questions with page: {}, size: {}", 
                request.getPage(), request.getPageSize());
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Question> questionPages = questionRepository.findAll(pageable);
        return buildPagingResponse(questionPages);
    }

    @Override
    public PagingResponse<QuestionResponse> getQuestionsByCategory(QuestionCategory category, PagingRequest request) {
        log.debug("QuestionService_getQuestionsByCategory_Fetching questions by category: {} with page: {}, size: {}", 
                category, request.getPage(), request.getPageSize());
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Question> questionPages = questionRepository.findByCategory(category, pageable);
        return buildPagingResponse(questionPages);
    }

    @Override
    public PagingResponse<QuestionResponse> getQuestionsByDifficulty(QuestionDifficulty difficulty, PagingRequest request) {
        log.debug("QuestionService_getQuestionsByDifficulty_Fetching questions by difficulty: {} with page: {}, size: {}", 
                difficulty, request.getPage(), request.getPageSize());
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Question> questionPages = questionRepository.findByDifficulty(difficulty, pageable);
        return buildPagingResponse(questionPages);
    }

    @Override
    public PagingResponse<QuestionResponse> getQuestionsByType(QuestionType questionType, PagingRequest request) {
        log.debug("QuestionService_getQuestionsByType_Fetching questions by type: {} with page: {}, size: {}", 
                questionType, request.getPage(), request.getPageSize());
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Question> questionPages = questionRepository.findByQuestionType(questionType, pageable);
        return buildPagingResponse(questionPages);
    }

    @Override
    public PagingResponse<QuestionResponse> getQuestionsByCriteria(QuestionCategory category,
                                                                   QuestionDifficulty difficulty,
                                                                   QuestionType questionType,
                                                                   PagingRequest request) {
        log.debug("QuestionService_getQuestionsByCriteria_Fetching questions by criteria with page: {}, size: {}", 
                request.getPage(), request.getPageSize());
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Question> questionPages = questionRepository.findByCriteria(category, difficulty, questionType, pageable);
        return buildPagingResponse(questionPages);
    }

    @Override
    public PagingResponse<QuestionResponse> searchQuestionsByContent(String keyword, PagingRequest request) {
        log.debug("QuestionService_searchQuestionsByContent_Searching questions with keyword: {} with page: {}, size: {}", 
                keyword, request.getPage(), request.getPageSize());
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Question> questionPages = questionRepository.findByContentContainingIgnoreCase(keyword, pageable);
        return buildPagingResponse(questionPages);
    }

    @Override
    public PagingResponse<QuestionResponse> getQuestionsByScoreRange(int minScore, int maxScore, PagingRequest request) {
        log.debug("QuestionService_getQuestionsByScoreRange_Fetching questions by score range [{}, {}] with page: {}, size: {}", 
                minScore, maxScore, request.getPage(), request.getPageSize());
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Question> questionPages = questionRepository.findByScoreBetween(minScore, maxScore, pageable);
        return buildPagingResponse(questionPages);
    }

    @Override
    public List<QuestionResponse> getRandomQuestions(QuestionCategory category,
                                                    QuestionDifficulty difficulty,
                                                    QuestionType questionType,
                                                    int limit) {
        List<Question> questions = questionRepository.findRandomQuestions(
                category != null ? category.name() : null,
                difficulty != null ? difficulty.name() : null,
                questionType != null ? questionType.name() : null,
                limit
        );

        return questions.stream()
                .map(questionMapper::toQuestionResponse)
                .toList();
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestion(String questionId, UpdateQuestionRequest request) {
        log.debug("QuestionService_updateQuestion_Updating question with id: {}", questionId);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> {
                    log.error("QuestionService_updateQuestion_Question not found with id: {}", questionId);
                    return QuestionErrorCode.QUESTION_NOT_FOUND.toException();
                });

        String userId = AuthUtils.getCurrentUserId();
        question.setUpdateBy(userId);

        // Update question fields
        questionMapper.updateQuestionFromRequest(question, request);

        // Update question options if provided
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            // Fetch existing options
            List<QuestionOption> existingOptions = questionOptionRepository.findByQuestionId(questionId);
            
            // Note: We do NOT delete AnswerReports when updating question options
            // AnswerReports should be preserved for history tracking
            // They will only be deleted when the question itself is deleted
            
            // Create a map of existing options by ID for quick lookup
            Map<String, QuestionOption> existingOptionsMap = existingOptions.stream()
                    .collect(Collectors.toMap(QuestionOption::getId, Function.identity()));
            
            List<QuestionOption> updatedOptions = new ArrayList<>();
            
            // Process each option from the request
            for (UpdateQuestionOptionRequest optionRequest : request.getOptions()) {
                if (optionRequest.getId() != null && existingOptionsMap.containsKey(optionRequest.getId())) {
                    // UPDATE existing option
                    QuestionOption existingOption = existingOptionsMap.get(optionRequest.getId());
                    questionOptionMapper.updateQuestionOptionFromRequest(existingOption, optionRequest);
                    existingOption.setUpdateBy(userId);
                    updatedOptions.add(questionOptionRepository.save(existingOption));
                } else {
                    // CREATE new option (no ID or ID doesn't exist)
                    QuestionOption newOption = questionOptionMapper.toQuestionOption(
                            CreateQuestionOptionRequest.builder()
                                    .content(optionRequest.getContent())
                                    .orderIndex(optionRequest.getOrderIndex())
                                    .isCorrect(optionRequest.getIsCorrect())
                                    .build()
                    );
                    newOption.setQuestion(question);
                    newOption.setCreatedBy(userId);
                    newOption.setUpdateBy(userId);
                    updatedOptions.add(questionOptionRepository.save(newOption));
                }
            }
            
            // Delete options that are not in the request (only if no AnswerReports reference them)
            for (QuestionOption existingOption : existingOptions) {
                boolean isInRequest = request.getOptions().stream()
                        .anyMatch(req -> req.getId() != null && req.getId().equals(existingOption.getId()));
                if (!isInRequest) {
                    // Check if option has AnswerReports
                    List<AnswerReport> reports = answerReportRepository.findByQuestionOptionId(
                            existingOption.getId(), Pageable.unpaged()).getContent();
                    if (reports.isEmpty()) {
                        // Safe to delete - no reports reference this option
                        questionOptionRepository.deleteById(existingOption.getId());
                        log.debug("QuestionService_updateQuestion_Deleted option {} (no AnswerReports)", existingOption.getId());
                    } else {
                        // Keep the option but don't add it to question.getOptions()
                        // Option will remain in database but not in question.getOptions()
                        log.debug("QuestionService_updateQuestion_Keeping option {} because it has {} reports", 
                                existingOption.getId(), reports.size());
                    }
                }
            }
            
            question.setOptions(updatedOptions);
        }

        QuestionResponse response = questionMapper.toQuestionResponse(questionRepository.save(question));
        log.debug("QuestionService_updateQuestion_Question updated successfully with id: {}", questionId);
        return response;
    }

    @Override
    @Transactional
    public void deleteQuestion(String questionId) {
        log.debug("QuestionService_deleteQuestion_Deleting question with id: {}", questionId);
        if (questionId == null || questionId.isBlank()) {
            log.error("QuestionService_deleteQuestion_Question ID is required");
            throw QuestionErrorCode.QUESTION_ID_REQUIRED.toException();
        }

        if (!questionRepository.existsById(questionId)) {
            log.error("QuestionService_deleteQuestion_Question not found with id: {}", questionId);
            throw QuestionErrorCode.QUESTION_NOT_FOUND.toException();
        }

        // Delete all AnswerReports related to this question first
        // This prevents foreign key constraint violation when deleting QuestionOption
        log.debug("QuestionService_deleteQuestion_Deleting answer reports for question id: {}", questionId);
        answerReportRepository.deleteByQuestionId(questionId);

        // Fetch and delete all AnswerReports related to question options of this question
        List<QuestionOption> questionOptions = questionOptionRepository.findByQuestionId(questionId);
        if (!questionOptions.isEmpty()) {
            log.debug("QuestionService_deleteQuestion_Deleting answer reports for {} question options", questionOptions.size());
            for (QuestionOption option : questionOptions) {
                answerReportRepository.deleteByQuestionOptionId(option.getId());
            }
        }

        // Now safe to delete the question (cascade will delete QuestionOptions)
        questionRepository.deleteById(questionId);
        log.debug("QuestionService_deleteQuestion_Question deleted successfully with id: {}", questionId);
    }

    private List<QuestionOption> createOptions(String userId, Question question, List<CreateQuestionOptionRequest> requests) {
            return requests.stream()
                    .map(optionRequest -> {
                        QuestionOption option = questionOptionMapper.toQuestionOption(optionRequest);
                        option.setQuestion(question);
                        option.setCreatedBy(userId);
                        option.setUpdateBy(userId);
                        return questionOptionRepository.save(option);
                    })
                    .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<QuestionOption> updateOptions(String userId, Question question, List<UpdateQuestionOptionRequest> requests) {
        return requests.stream()
                .map(optionRequest -> {
                    QuestionOption option = questionOptionMapper.toQuestionOption(
                            CreateQuestionOptionRequest.builder()
                                    .content(optionRequest.getContent())
                                    .orderIndex(optionRequest.getOrderIndex())
                                    .isCorrect(optionRequest.getIsCorrect())
                                    .build()
                    );
                    option.setQuestion(question);
                    option.setCreatedBy(userId);
                    option.setUpdateBy(userId);
                    return option;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }
    
    @Override
    @Transactional
    public List<String> createQuestions(List<CreateQuestionRequest> requests, String courseId) {
        log.debug("QuestionService_createQuestions_Creating {} questions for course: {}", requests.size(), courseId);
        String userId = AuthUtils.getCurrentUserId();
        List<String> questionIds = new ArrayList<>();
        int newCount = 0;
        int existingCount = 0;
        
        for (CreateQuestionRequest request : requests) {
            // Validate options based on question type
            // CONTENT type questions don't require options, other types do
            if (request.getQuestionType() != QuestionType.CONTENT) {
                if (request.getOptions() == null || request.getOptions().isEmpty()) {
                    log.error("QuestionService_createQuestions_Options are required for question type: {}", request.getQuestionType());
                    throw QuestionErrorCode.QUESTION_TYPE_INVALID.toException();
                }
            }
            
            // Extract option contents for hash generation
            List<String> optionContents = request.getOptions() != null 
                    ? request.getOptions().stream()
                            .map(CreateQuestionOptionRequest::getContent)
                            .toList()
                    : new ArrayList<>();
            
            // Generate hash from question content and options
            String contentHash = QuestionHashUtil.generateQuestionHash(request.getContent(), optionContents);
            String normalizedText = QuestionHashUtil.buildHashContent(request.getContent(), optionContents);
            
            // Check if question already exists in this course
            Question existingQuestion = null;
            if (courseId != null && !courseId.trim().isEmpty()) {
                existingQuestion = questionRepository.findByContentHashAndCourseId(contentHash, courseId)
                        .orElse(null);
            } else {
                // If no course, check globally
                existingQuestion = questionRepository.findByContentHash(contentHash)
                        .orElse(null);
            }
            
            if (existingQuestion != null) {
                // Question already exists, use existing ID
                log.debug("QuestionService_createQuestions_Question already exists with hash: {}, using ID: {}", 
                        contentHash, existingQuestion.getId());
                questionIds.add(existingQuestion.getId());
                existingCount++;
            } else {
                // Create new question
                Question question = questionMapper.toQuestion(request);
                question.setContentHash(contentHash);
                question.setNormalizedText(normalizedText);
                question.setCreatedBy(userId);
                question.setUpdateBy(userId);
                
                Question savedQuestion = questionRepository.save(question);
                
                // Create and save question options if provided
                if (request.getOptions() != null && !request.getOptions().isEmpty()) {
                    savedQuestion.setOptions(createOptions(userId, savedQuestion, request.getOptions()));
                }
                
                questionIds.add(savedQuestion.getId());
                newCount++;
                log.debug("QuestionService_createQuestions_New question created with ID: {}", savedQuestion.getId());
            }
        }
        
        log.debug("QuestionService_createQuestions_Created {} questions ({} new, {} existing)", 
                questionIds.size(), newCount, existingCount);
        
        return questionIds;
    }
}
