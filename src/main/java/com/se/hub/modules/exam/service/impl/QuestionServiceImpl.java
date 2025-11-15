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
import com.se.hub.modules.exam.entity.Question;
import com.se.hub.modules.exam.entity.QuestionOption;
import com.se.hub.modules.exam.enums.QuestionCategory;
import com.se.hub.modules.exam.enums.QuestionDifficulty;
import com.se.hub.modules.exam.enums.QuestionType;
import com.se.hub.modules.exam.exception.QuestionErrorCode;
import com.se.hub.modules.exam.mapper.QuestionMapper;
import com.se.hub.modules.exam.mapper.QuestionOptionMapper;
import com.se.hub.modules.exam.repository.QuestionOptionRepository;
import com.se.hub.modules.exam.repository.QuestionRepository;
import com.se.hub.modules.exam.service.QuestionService;
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
        Question question = questionMapper.toQuestion(request);
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
            // Delete existing options from database
            questionOptionRepository.deleteByQuestionId(questionId);
            
            // Clear the collection in memory (create new ArrayList to avoid immutable collection issue)
            question.setOptions(new ArrayList<>());
            
            // Create and save new options
            List<QuestionOption> options = updateOptions(userId, question, request.getOptions());
            // Save options to database before setting them on the question
            List<QuestionOption> savedOptions = options.stream()
                    .map(questionOptionRepository::save)
                    .collect(Collectors.toCollection(ArrayList::new));
            question.setOptions(savedOptions);
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
}
