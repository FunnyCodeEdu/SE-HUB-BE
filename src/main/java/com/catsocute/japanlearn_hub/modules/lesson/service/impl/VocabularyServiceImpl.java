package com.catsocute.japanlearn_hub.modules.lesson.service.impl;

import com.catsocute.japanlearn_hub.common.constant.GlobalVariable;
import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.common.enums.ErrorCode;
import com.catsocute.japanlearn_hub.common.exception.AppException;
import com.catsocute.japanlearn_hub.common.utils.PagingUtil;
import com.catsocute.japanlearn_hub.modules.auth.utils.AuthUtils;
import com.catsocute.japanlearn_hub.modules.lesson.dto.request.AddContentToLessonRequest;
import com.catsocute.japanlearn_hub.modules.lesson.dto.request.CreateVocabularyRequest;
import com.catsocute.japanlearn_hub.modules.lesson.dto.request.UpdateVocabularyRequest;
import com.catsocute.japanlearn_hub.modules.lesson.dto.response.VocabularyResponse;
import com.catsocute.japanlearn_hub.modules.lesson.entity.Lesson;
import com.catsocute.japanlearn_hub.modules.lesson.entity.Vocabulary;
import com.catsocute.japanlearn_hub.modules.lesson.enums.JLPTLevel;
import com.catsocute.japanlearn_hub.modules.lesson.enums.VocabularyType;
import com.catsocute.japanlearn_hub.modules.lesson.mapper.VocabularyMapper;
import com.catsocute.japanlearn_hub.modules.lesson.repository.LessonRepository;
import com.catsocute.japanlearn_hub.modules.lesson.repository.VocabularyRepository;
import com.catsocute.japanlearn_hub.modules.lesson.service.api.VocabularyService;
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
public class VocabularyServiceImpl implements VocabularyService {
    VocabularyRepository vocabularyRepository;
    LessonRepository lessonRepository;
    VocabularyMapper vocabularyMapper;

    @Override
    public VocabularyResponse createVocabulary(CreateVocabularyRequest request) {
        Vocabulary vocabulary = vocabularyMapper.toVocabulary(request);
        String userId = AuthUtils.getCurrentUserId();
        vocabulary.setCreatedBy(userId);
        vocabulary.setUpdateBy(userId);

        return vocabularyMapper.toVocabularyResponse(vocabularyRepository.save(vocabulary));
    }

    @Override
    public VocabularyResponse getById(String vocabularyId) {
        return vocabularyMapper.toVocabularyResponse(vocabularyRepository.findById(vocabularyId)
                .orElseThrow(() -> {
                    log.error("Vocabulary id {} not found ", vocabularyId);
                    return new AppException(ErrorCode.VOCABULARY_NOT_FOUND);
                }));
    }

    @Override
    public PagingResponse<VocabularyResponse> getVocabulariesByLevel(String level, PagingRequest request) {
        JLPTLevel jlptLevel;
        try {
            jlptLevel = JLPTLevel.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid JLPT level: {}", level);
            throw new AppException(ErrorCode.VOCABULARY_LEVEL_INVALID);
        }

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Vocabulary> vocabularies = vocabularyRepository.findByLevel(jlptLevel, pageable);

        return PagingResponse.<VocabularyResponse>builder()
                .currentPage(vocabularies.getNumber())
                .totalPages(vocabularies.getTotalPages())
                .pageSize(vocabularies.getSize())
                .totalElement(vocabularies.getTotalElements())
                .data(vocabularies.getContent().stream()
                        .map(vocabularyMapper::toVocabularyResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<VocabularyResponse> getVocabulariesByType(String type, PagingRequest request) {
        VocabularyType vocabularyType;
        try {
            vocabularyType = VocabularyType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid vocabulary type: {}", type);
            throw new AppException(ErrorCode.VOCABULARY_TYPE_INVALID);
        }

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Vocabulary> vocabularies = vocabularyRepository.findByType(vocabularyType, pageable);

        return PagingResponse.<VocabularyResponse>builder()
                .currentPage(vocabularies.getNumber())
                .totalPages(vocabularies.getTotalPages())
                .pageSize(vocabularies.getSize())
                .totalElement(vocabularies.getTotalElements())
                .data(vocabularies.getContent().stream()
                        .map(vocabularyMapper::toVocabularyResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<VocabularyResponse> getVocabularies(PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Vocabulary> vocabularies = vocabularyRepository.findAll(pageable);

        return PagingResponse.<VocabularyResponse>builder()
                .currentPage(vocabularies.getNumber())
                .totalPages(vocabularies.getTotalPages())
                .pageSize(vocabularies.getSize())
                .totalElement(vocabularies.getTotalElements())
                .data(vocabularies.getContent().stream()
                        .map(vocabularyMapper::toVocabularyResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public VocabularyResponse updateVocabularyById(String vocabularyId, UpdateVocabularyRequest request) {
        Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId)
                .orElseThrow(() -> {
                    log.error(" Vocabulary id {} not found", vocabularyId);
                    return new AppException(ErrorCode.VOCABULARY_NOT_FOUND);
                });

        vocabularyMapper.updateVocabularyFromRequest(vocabulary, request);

        return vocabularyMapper.toVocabularyResponse(vocabularyRepository.save(vocabulary));
    }

    @Override
    public void deleteVocabularyById(String vocabularyId) {
        vocabularyRepository.deleteById(vocabularyId);
    }

    @Override
    public void addContent(String lessonId, AddContentToLessonRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        List<Vocabulary> vocabularies = vocabularyRepository.findAllById(request.getContentIds());
        lesson.setVocabularies(new HashSet<>(vocabularies));
        lessonRepository.save(lesson);
    }
}
