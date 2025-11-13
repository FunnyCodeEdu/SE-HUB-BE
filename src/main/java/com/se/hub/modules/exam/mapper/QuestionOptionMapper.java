package com.se.hub.modules.exam.mapper;

import com.se.hub.modules.exam.dto.request.CreateQuestionOptionRequest;
import com.se.hub.modules.exam.dto.request.UpdateQuestionOptionRequest;
import com.se.hub.modules.exam.dto.response.QuestionOptionResponse;
import com.se.hub.modules.exam.entity.QuestionOption;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface QuestionOptionMapper {
    QuestionOption toQuestionOption(CreateQuestionOptionRequest request);
    QuestionOptionResponse toQuestionOptionResponse(QuestionOption questionOption);
    void updateQuestionOptionFromRequest(@MappingTarget QuestionOption questionOption, UpdateQuestionOptionRequest request);
}