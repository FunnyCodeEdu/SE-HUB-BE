package com.catsocute.japanlearn_hub.modules.lesson.service.impl;

import com.catsocute.japanlearn_hub.modules.lesson.enums.LessonType;
import com.catsocute.japanlearn_hub.modules.lesson.service.api.BaseContentService;
import com.catsocute.japanlearn_hub.modules.lesson.service.api.ContentServiceFactory;
import com.catsocute.japanlearn_hub.modules.lesson.service.api.GrammarService;
import com.catsocute.japanlearn_hub.modules.lesson.service.api.VocabularyService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentServiceFactoryImpl implements ContentServiceFactory {
    GrammarService grammarService;
    VocabularyService vocabularyService;

    @Override
    public BaseContentService getService(LessonType lessonType) {
        log.info("getService lessonType={}", lessonType);
        return switch (lessonType) {
            case GRAMMAR -> grammarService;
            case VOCABULARY,ALPHABETIC -> vocabularyService;
            default -> null;
        };
    }
}
