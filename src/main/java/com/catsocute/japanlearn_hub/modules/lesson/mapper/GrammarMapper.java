package com.catsocute.japanlearn_hub.modules.lesson.mapper;

import com.catsocute.japanlearn_hub.modules.lesson.dto.request.CreateGrammarRequest;
import com.catsocute.japanlearn_hub.modules.lesson.dto.request.UpdateGrammarRequest;
import com.catsocute.japanlearn_hub.modules.lesson.dto.response.GrammarResponse;
import com.catsocute.japanlearn_hub.modules.lesson.entity.Grammar;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GrammarMapper {
    Grammar toGrammar(CreateGrammarRequest request);
    GrammarResponse toGrammarResponse(Grammar grammar);
    void updateGrammarFromRequest(@MappingTarget Grammar grammar, UpdateGrammarRequest request);
}
