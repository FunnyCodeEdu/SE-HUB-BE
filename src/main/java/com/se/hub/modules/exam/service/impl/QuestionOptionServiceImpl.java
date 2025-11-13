package com.se.hub.modules.exam.service.impl;

import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.exam.dto.request.CreateQuestionOptionRequest;
import com.se.hub.modules.exam.dto.request.UpdateQuestionOptionRequest;
import com.se.hub.modules.exam.dto.response.QuestionOptionResponse;
import com.se.hub.modules.exam.entity.Question;
import com.se.hub.modules.exam.entity.QuestionOption;
import com.se.hub.modules.exam.mapper.QuestionOptionMapper;
import com.se.hub.modules.exam.repository.QuestionOptionRepository;
import com.se.hub.modules.exam.service.api.QuestionOptionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        QuestionOption questionOption = questionOptionMapper.toQuestionOption(request);
        String userId = AuthUtils.getCurrentUserId();
        questionOption.setCreatedBy(userId);
        questionOption.setUpdateBy(userId);
        questionOption.setQuestion(question);

        return questionOptionMapper.toQuestionOptionResponse(questionOptionRepository.save(questionOption));
    }

    @Override
    public QuestionOptionResponse getById(String optionId) {
        return questionOptionMapper.toQuestionOptionResponse(questionOptionRepository.findById(optionId)
                .orElseThrow(() -> {
                    log.error("QuestionOptionServiceImpl_getById_Question option id {} not found", optionId);
                    return new AppException(ErrorCode.QUESTION_OPTION_NOT_FOUND);
                }));
    }

    @Override
    public List<QuestionOptionResponse> getOptionsByQuestionId(String questionId) {
        return questionOptionRepository.findByQuestionIdOrderByOrderIndex(questionId)
                .stream()
                .map(questionOptionMapper::toQuestionOptionResponse)
                .toList();
    }

    @Override
    public List<QuestionOptionResponse> getCorrectOptionsByQuestionId(String questionId) {
        return questionOptionRepository.findByQuestionIdAndIsCorrectTrue(questionId)
                .stream()
                .map(questionOptionMapper::toQuestionOptionResponse)
                .toList();
    }

    @Override
    @Transactional
    public QuestionOptionResponse updateQuestionOption(String optionId, UpdateQuestionOptionRequest request) {
        QuestionOption questionOption = questionOptionRepository.findById(optionId)
                .orElseThrow(() -> {
                    log.error("QuestionOptionServiceImpl_updateQuestionOption_Question option id {} not found", optionId);
                    return new AppException(ErrorCode.QUESTION_OPTION_NOT_FOUND);
                });

        String userId = AuthUtils.getCurrentUserId();
        questionOption.setUpdateBy(userId);

        questionOptionMapper.updateQuestionOptionFromRequest(questionOption, request);

        return questionOptionMapper.toQuestionOptionResponse(questionOptionRepository.save(questionOption));
    }

    @Override
    @Transactional
    public void deleteQuestionOption(String optionId) {
        questionOptionRepository.deleteById(optionId);
    }

    @Override
    @Transactional
    public void deleteOptionsByQuestionId(String questionId) {
        questionOptionRepository.deleteByQuestionId(questionId);
    }
}
