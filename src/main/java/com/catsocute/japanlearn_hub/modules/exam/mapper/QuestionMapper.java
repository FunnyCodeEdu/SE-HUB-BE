package com.catsocute.japanlearn_hub.modules.exam.mapper;

import com.catsocute.japanlearn_hub.modules.exam.dto.request.CreateQuestionRequest;
import com.catsocute.japanlearn_hub.modules.exam.dto.request.UpdateQuestionRequest;
import com.catsocute.japanlearn_hub.modules.exam.dto.response.QuestionResponse;
import com.catsocute.japanlearn_hub.modules.exam.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {QuestionOptionMapper.class})
public interface QuestionMapper {
    Question toQuestion(CreateQuestionRequest request);
    QuestionResponse toQuestionResponse(Question question);
    void updateQuestionFromRequest(@MappingTarget Question question, UpdateQuestionRequest request);
}