package com.se.hub.modules.exam.service.impl;

import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.exam.dto.request.CreateQuestionOptionRequest;
import com.se.hub.modules.exam.dto.request.UpdateQuestionOptionRequest;
import com.se.hub.modules.exam.dto.response.QuestionOptionResponse;
import com.se.hub.modules.exam.entity.Question;
import com.se.hub.modules.exam.entity.QuestionOption;
import com.se.hub.modules.exam.exception.QuestionOptionErrorCode;
import com.se.hub.modules.exam.mapper.QuestionOptionMapper;
import com.se.hub.modules.exam.repository.QuestionOptionRepository;
import com.se.hub.modules.exam.service.QuestionOptionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * QuestionOption Service Implementation
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
public class QuestionOptionServiceImpl implements QuestionOptionService {
    QuestionOptionRepository questionOptionRepository;
    QuestionOptionMapper questionOptionMapper;

    @Override
    @Transactional
    public QuestionOptionResponse createQuestionOption(Question question, CreateQuestionOptionRequest request) {
        log.debug("QuestionOptionService_createQuestionOption_Creating new question option for user: {}", AuthUtils.getCurrentUserId());
        QuestionOption questionOption = questionOptionMapper.toQuestionOption(request);
        String userId = AuthUtils.getCurrentUserId();
        questionOption.setCreatedBy(userId);
        questionOption.setUpdateBy(userId);
        questionOption.setQuestion(question);

        QuestionOptionResponse response = questionOptionMapper.toQuestionOptionResponse(questionOptionRepository.save(questionOption));
        log.debug("QuestionOptionService_createQuestionOption_Question option created successfully with id: {}", response.getId());
        return response;
    }

    @Override
    public QuestionOptionResponse getById(String optionId) {
        log.debug("QuestionOptionService_getById_Fetching question option with id: {}", optionId);
        return questionOptionMapper.toQuestionOptionResponse(questionOptionRepository.findById(optionId)
                .orElseThrow(() -> {
                    log.error("QuestionOptionService_getById_Question option not found with id: {}", optionId);
                    return QuestionOptionErrorCode.QUESTION_OPTION_NOT_FOUND.toException();
                }));
    }

    @Override
    public List<QuestionOptionResponse> getOptionsByQuestionId(String questionId) {
        log.debug("QuestionOptionService_getOptionsByQuestionId_Fetching options for question: {}", questionId);
        return questionOptionRepository.findByQuestionIdOrderByOrderIndex(questionId)
                .stream()
                .map(questionOptionMapper::toQuestionOptionResponse)
                .toList();
    }

    @Override
    public List<QuestionOptionResponse> getCorrectOptionsByQuestionId(String questionId) {
        log.debug("QuestionOptionService_getCorrectOptionsByQuestionId_Fetching correct options for question: {}", questionId);
        return questionOptionRepository.findByQuestionIdAndIsCorrectTrue(questionId)
                .stream()
                .map(questionOptionMapper::toQuestionOptionResponse)
                .toList();
    }

    @Override
    @Transactional
    public QuestionOptionResponse updateQuestionOption(String optionId, UpdateQuestionOptionRequest request) {
        log.debug("QuestionOptionService_updateQuestionOption_Updating question option with id: {}", optionId);
        QuestionOption questionOption = questionOptionRepository.findById(optionId)
                .orElseThrow(() -> {
                    log.error("QuestionOptionService_updateQuestionOption_Question option not found with id: {}", optionId);
                    return QuestionOptionErrorCode.QUESTION_OPTION_NOT_FOUND.toException();
                });

        String userId = AuthUtils.getCurrentUserId();
        questionOption.setUpdateBy(userId);

        questionOptionMapper.updateQuestionOptionFromRequest(questionOption, request);

        QuestionOptionResponse response = questionOptionMapper.toQuestionOptionResponse(questionOptionRepository.save(questionOption));
        log.debug("QuestionOptionService_updateQuestionOption_Question option updated successfully with id: {}", optionId);
        return response;
    }

    @Override
    @Transactional
    public void deleteQuestionOption(String optionId) {
        log.debug("QuestionOptionService_deleteQuestionOption_Deleting question option with id: {}", optionId);
        if (optionId == null || optionId.isBlank()) {
            log.error("QuestionOptionService_deleteQuestionOption_Question option ID is required");
            throw QuestionOptionErrorCode.QUESTION_OPTION_ID_REQUIRED.toException();
        }

        if (!questionOptionRepository.existsById(optionId)) {
            log.error("QuestionOptionService_deleteQuestionOption_Question option not found with id: {}", optionId);
            throw QuestionOptionErrorCode.QUESTION_OPTION_NOT_FOUND.toException();
        }

        questionOptionRepository.deleteById(optionId);
        log.debug("QuestionOptionService_deleteQuestionOption_Question option deleted successfully with id: {}", optionId);
    }

    @Override
    @Transactional
    public void deleteOptionsByQuestionId(String questionId) {
        log.debug("QuestionOptionService_deleteOptionsByQuestionId_Deleting all options for question: {}", questionId);
        questionOptionRepository.deleteByQuestionId(questionId);
        log.debug("QuestionOptionService_deleteOptionsByQuestionId_All options deleted successfully for question: {}", questionId);
    }
}
