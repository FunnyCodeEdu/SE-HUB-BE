package com.se.hub.modules.lesson.mapper;

import com.se.hub.modules.lesson.dto.request.CreateGrammarRequest;
import com.se.hub.modules.lesson.dto.request.UpdateGrammarRequest;
import com.se.hub.modules.lesson.dto.response.GrammarResponse;
import com.se.hub.modules.lesson.entity.Grammar;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GrammarMapper {
    Grammar toGrammar(CreateGrammarRequest request);
    GrammarResponse toGrammarResponse(Grammar grammar);
    void updateGrammarFromRequest(@MappingTarget Grammar grammar, UpdateGrammarRequest request);
}
