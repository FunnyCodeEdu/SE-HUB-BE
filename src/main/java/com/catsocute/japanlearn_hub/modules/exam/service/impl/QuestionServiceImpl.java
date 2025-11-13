package com.catsocute.japanlearn_hub.modules.exam.service.impl;

import com.catsocute.japanlearn_hub.common.constant.GlobalVariable;
import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.common.enums.ErrorCode;
import com.catsocute.japanlearn_hub.common.exception.AppException;
import com.catsocute.japanlearn_hub.modules.auth.utils.AuthUtils;
import com.catsocute.japanlearn_hub.common.utils.PagingUtil;
import com.catsocute.japanlearn_hub.modules.exam.dto.request.CreateQuestionRequest;
import com.catsocute.japanlearn_hub.modules.exam.dto.request.CreateQuestionOptionRequest;
import com.catsocute.japanlearn_hub.modules.exam.dto.request.UpdateQuestionOptionRequest;
import com.catsocute.japanlearn_hub.modules.exam.dto.request.UpdateQuestionRequest;
import com.catsocute.japanlearn_hub.modules.exam.dto.response.QuestionResponse;
import com.catsocute.japanlearn_hub.modules.exam.entity.Question;
import com.catsocute.japanlearn_hub.modules.exam.entity.QuestionOption;
import com.catsocute.japanlearn_hub.modules.exam.enums.QuestionCategory;
import com.catsocute.japanlearn_hub.modules.exam.enums.QuestionDifficulty;
import com.catsocute.japanlearn_hub.modules.exam.enums.QuestionType;
import com.catsocute.japanlearn_hub.modules.exam.mapper.QuestionMapper;
import com.catsocute.japanlearn_hub.modules.exam.mapper.QuestionOptionMapper;
import com.catsocute.japanlearn_hub.modules.exam.repository.QuestionOptionRepository;
import com.catsocute.japanlearn_hub.modules.exam.repository.QuestionRepository;
import com.catsocute.japanlearn_hub.modules.exam.service.api.QuestionService;
import com.catsocute.japanlearn_hub.modules.lesson.enums.JLPTLevel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionServiceImpl implements QuestionService {
    QuestionRepository questionRepository;
    QuestionOptionRepository  questionOptionRepository;
    QuestionMapper questionMapper;
    QuestionOptionMapper questionOptionMapper;

    @Override
    @Transactional
    public QuestionResponse createQuestion(CreateQuestionRequest request) {
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

        return questionMapper.toQuestionResponse(savedQuestion);
    }

    @Override
    public QuestionResponse getById(String questionId) {
        return questionMapper.toQuestionResponse(questionRepository.findById(questionId)
                .orElseThrow(() -> {
                    log.error("QuestionServiceImpl_getById_Question id {} not found", questionId);
                    return new AppException(ErrorCode.QUESTION_NOT_FOUND);
                }));
    }

    @Override
    public PagingResponse<QuestionResponse> getAllQuestions(PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Question> questionPages = questionRepository.findAll(pageable);

        return PagingResponse.<QuestionResponse>builder()
                .currentPage(request.getPage())
                .pageSize(questionPages.getSize())
                .totalPages(questionPages.getTotalPages())
                .totalElement(questionPages.getTotalElements())
                .data(questionPages.getContent().stream()
                        .map(questionMapper::toQuestionResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<QuestionResponse> getQuestionsByCategory(QuestionCategory category, PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Question> questionPages = questionRepository.findByCategory(category, pageable);

        return PagingResponse.<QuestionResponse>builder()
                .currentPage(request.getPage())
                .pageSize(questionPages.getSize())
                .totalPages(questionPages.getTotalPages())
                .totalElement(questionPages.getTotalElements())
                .data(questionPages.getContent().stream()
                        .map(questionMapper::toQuestionResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<QuestionResponse> getQuestionsByDifficulty(QuestionDifficulty difficulty, PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Question> questionPages = questionRepository.findByDifficulty(difficulty, pageable);

        return PagingResponse.<QuestionResponse>builder()
                .currentPage(request.getPage())
                .pageSize(questionPages.getSize())
                .totalPages(questionPages.getTotalPages())
                .totalElement(questionPages.getTotalElements())
                .data(questionPages.getContent().stream()
                        .map(questionMapper::toQuestionResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<QuestionResponse> getQuestionsByJlptLevel(JLPTLevel jlptLevel, PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Question> questionPages = questionRepository.findByJlptLevel(jlptLevel, pageable);

        return PagingResponse.<QuestionResponse>builder()
                .currentPage(request.getPage())
                .pageSize(questionPages.getSize())
                .totalPages(questionPages.getTotalPages())
                .totalElement(questionPages.getTotalElements())
                .data(questionPages.getContent().stream()
                        .map(questionMapper::toQuestionResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<QuestionResponse> getQuestionsByType(QuestionType questionType, PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Question> questionPages = questionRepository.findByQuestionType(questionType, pageable);

        return PagingResponse.<QuestionResponse>builder()
                .currentPage(request.getPage())
                .pageSize(questionPages.getSize())
                .totalPages(questionPages.getTotalPages())
                .totalElement(questionPages.getTotalElements())
                .data(questionPages.getContent().stream()
                        .map(questionMapper::toQuestionResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<QuestionResponse> getQuestionsByCriteria(QuestionCategory category,
                                                                   QuestionDifficulty difficulty,
                                                                   JLPTLevel jlptLevel,
                                                                   QuestionType questionType,
                                                                   PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Question> questionPages = questionRepository.findByCriteria(category, difficulty, jlptLevel, questionType, pageable);

        return PagingResponse.<QuestionResponse>builder()
                .currentPage(request.getPage())
                .pageSize(questionPages.getSize())
                .totalPages(questionPages.getTotalPages())
                .totalElement(questionPages.getTotalElements())
                .data(questionPages.getContent().stream()
                        .map(questionMapper::toQuestionResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<QuestionResponse> searchQuestionsByContent(String keyword, PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Question> questionPages = questionRepository.findByContentContainingIgnoreCase(keyword, pageable);

        return PagingResponse.<QuestionResponse>builder()
                .currentPage(request.getPage())
                .pageSize(questionPages.getSize())
                .totalPages(questionPages.getTotalPages())
                .totalElement(questionPages.getTotalElements())
                .data(questionPages.getContent().stream()
                        .map(questionMapper::toQuestionResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<QuestionResponse> getQuestionsByScoreRange(int minScore, int maxScore, PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Question> questionPages = questionRepository.findByScoreBetween(minScore, maxScore, pageable);

        return PagingResponse.<QuestionResponse>builder()
                .currentPage(request.getPage())
                .pageSize(questionPages.getSize())
                .totalPages(questionPages.getTotalPages())
                .totalElement(questionPages.getTotalElements())
                .data(questionPages.getContent().stream()
                        .map(questionMapper::toQuestionResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public List<QuestionResponse> getRandomQuestions(QuestionCategory category,
                                                    QuestionDifficulty difficulty,
                                                    JLPTLevel jlptLevel,
                                                    QuestionType questionType,
                                                    int limit) {
        List<Question> questions = questionRepository.findRandomQuestions(
                category != null ? category.name() : null,
                difficulty != null ? difficulty.name() : null,
                jlptLevel != null ? jlptLevel.name() : null,
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
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> {
                    log.error("QuestionServiceImpl_updateQuestion_Question id {} not found", questionId);
                    return new AppException(ErrorCode.QUESTION_NOT_FOUND);
                });

        String userId = AuthUtils.getCurrentUserId();
        question.setUpdateBy(userId);

        // Update question fields
        questionMapper.updateQuestionFromRequest(question, request);

        // Update question options if provided
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            // Delete existing options
            question.getOptions().clear();
            
            // Add new options
            List<QuestionOption> options = updateOptions(userId, question,  request.getOptions());
            question.setOptions(options);
        }

        return questionMapper.toQuestionResponse(questionRepository.save(question));
    }

    @Override
    @Transactional
    public void deleteQuestion(String questionId) {
        questionRepository.deleteById(questionId);
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
                    .toList();
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
                .toList();
    }
}
