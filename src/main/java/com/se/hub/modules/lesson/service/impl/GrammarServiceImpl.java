package com.se.hub.modules.lesson.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.lesson.dto.request.AddContentToLessonRequest;
import com.se.hub.modules.lesson.dto.request.CreateGrammarRequest;
import com.se.hub.modules.lesson.dto.request.UpdateGrammarRequest;
import com.se.hub.modules.lesson.dto.response.GrammarResponse;
import com.se.hub.modules.lesson.entity.Grammar;
import com.se.hub.modules.lesson.entity.Lesson;
import com.se.hub.modules.lesson.enums.JLPTLevel;
import com.se.hub.modules.lesson.mapper.GrammarMapper;
import com.se.hub.modules.lesson.repository.GrammarRepository;
import com.se.hub.modules.lesson.repository.LessonRepository;
import com.se.hub.modules.lesson.service.api.GrammarService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GrammarServiceImpl implements GrammarService {
    GrammarRepository grammarRepository;
    LessonRepository lessonRepository;
    GrammarMapper grammarMapper;

    @Override
    public GrammarResponse createGrammar(CreateGrammarRequest request) {
        if(grammarRepository.existsByTitle(request.getTitle())) {
            log.error("Grammar title {} already exists", request.getTitle());
            throw new AppException(ErrorCode.GRAMMAR_TITLE_EXISTED);
        }
        Grammar grammar = grammarMapper.toGrammar(request);
        String userId = AuthUtils.getCurrentUserId();
        grammar.setCreatedBy(userId);
        grammar.setUpdateBy(userId);

        return grammarMapper.toGrammarResponse(grammarRepository.save(grammar));
    }

    @Override
    public GrammarResponse getById(String grammarId) {
        return grammarMapper.toGrammarResponse(grammarRepository.findById(grammarId)
                .orElseThrow(() -> {
                    log.error("Grammar id {} not found ", grammarId);
                    return new AppException(ErrorCode.GRAMMAR_NOT_FOUND);
                }));
    }

    @Override
    public PagingResponse<GrammarResponse> getGrammarsByLevel(String level, PagingRequest request) {
        JLPTLevel jlptLevel;
        try {
            jlptLevel = JLPTLevel.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid JLPT level: {}", level);
            throw new AppException(ErrorCode.GRAMMAR_LEVEL_INVALID);
        }

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Grammar> grammars = grammarRepository.findByLevel(jlptLevel, pageable);

        return PagingResponse.<GrammarResponse>builder()
                .currentPage(grammars.getNumber())
                .totalPages(grammars.getTotalPages())
                .pageSize(grammars.getSize())
                .totalElement(grammars.getTotalElements())
                .data(grammars.getContent().stream()
                        .map(grammarMapper::toGrammarResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<GrammarResponse> getGrammars(PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Grammar> grammars = grammarRepository.findAll(pageable);

        return PagingResponse.<GrammarResponse>builder()
                .currentPage(grammars.getNumber())
                .totalPages(grammars.getTotalPages())
                .pageSize(grammars.getSize())
                .totalElement(grammars.getTotalElements())
                .data(grammars.getContent().stream()
                        .map(grammarMapper::toGrammarResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public GrammarResponse updateGrammarById(String grammarId, UpdateGrammarRequest request) {
        Grammar grammar = grammarRepository.findById(grammarId)
                .orElseThrow(() -> {
                    log.error(" Grammar id {} not found", grammarId);
                    return new AppException(ErrorCode.GRAMMAR_NOT_FOUND);
                });

        grammarMapper.updateGrammarFromRequest(grammar, request);

        return grammarMapper.toGrammarResponse(grammarRepository.save(grammar));
    }

    @Override
    public void deleteGrammarById(String grammarId) {
        grammarRepository.deleteById(grammarId);
    }

    @Override
    public void addContent(String lessonId, AddContentToLessonRequest request) {
        log.info("Adding content to lesson {} ", lessonId);
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        List<Grammar> grammars = grammarRepository.findAllById(request.getContentIds());
        log.info("Found {} grammars", grammars.size());

        //set grammars to lesson
        lesson.setGrammars(new HashSet<>(grammars));
        lessonRepository.save(lesson);
    }
}
