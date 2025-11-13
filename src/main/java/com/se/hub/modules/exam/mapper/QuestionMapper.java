package com.se.hub.modules.exam.mapper;

import com.se.hub.modules.exam.dto.request.CreateQuestionRequest;
import com.se.hub.modules.exam.dto.request.UpdateQuestionRequest;
import com.se.hub.modules.exam.dto.response.QuestionResponse;
import com.se.hub.modules.exam.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {QuestionOptionMapper.class})
public interface QuestionMapper {
    Question toQuestion(CreateQuestionRequest request);
    QuestionResponse toQuestionResponse(Question question);
    void updateQuestionFromRequest(@MappingTarget Question question, UpdateQuestionRequest request);
}