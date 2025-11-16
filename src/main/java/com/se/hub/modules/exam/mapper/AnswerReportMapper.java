package com.se.hub.modules.exam.mapper;

import com.se.hub.modules.exam.dto.request.CreateAnswerReportRequest;
import com.se.hub.modules.exam.dto.response.AnswerReportResponse;
import com.se.hub.modules.exam.entity.AnswerReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnswerReportMapper {
    
    @Mapping(target = "question", ignore = true)
    @Mapping(target = "questionOption", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "adminId", ignore = true)
    @Mapping(target = "reporterId", ignore = true)
    AnswerReport toAnswerReport(CreateAnswerReportRequest request);

    @Mapping(target = "questionId", source = "question.id")
    @Mapping(target = "questionContent", source = "question.content")
    @Mapping(target = "questionOptionId", source = "questionOption.id")
    @Mapping(target = "questionOptionContent", source = "questionOption.content")
    @Mapping(target = "status", source = "status", defaultValue = "PENDING")
    AnswerReportResponse toAnswerReportResponse(AnswerReport answerReport);
}

