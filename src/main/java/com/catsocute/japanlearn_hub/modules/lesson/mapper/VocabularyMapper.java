package com.catsocute.japanlearn_hub.modules.lesson.mapper;

import com.catsocute.japanlearn_hub.modules.lesson.dto.request.CreateVocabularyRequest;
import com.catsocute.japanlearn_hub.modules.lesson.dto.request.UpdateVocabularyRequest;
import com.catsocute.japanlearn_hub.modules.lesson.dto.response.VocabularyResponse;
import com.catsocute.japanlearn_hub.modules.lesson.entity.Vocabulary;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VocabularyMapper {
    Vocabulary toVocabulary(CreateVocabularyRequest request);
    VocabularyResponse toVocabularyResponse(Vocabulary vocabulary);
    void updateVocabularyFromRequest(@MappingTarget Vocabulary vocabulary, UpdateVocabularyRequest request);
}
