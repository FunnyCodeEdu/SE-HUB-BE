package com.catsocute.japanlearn_hub.modules.exam.mapper;

import com.catsocute.japanlearn_hub.modules.exam.dto.response.ExamResultResponse;
import com.catsocute.japanlearn_hub.modules.exam.entity.ExamAttempt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExamAttemptMapper {
    
    @Mapping(target = "attemptId", source = "id")
    @Mapping(target = "examId", source = "exam.id")
    @Mapping(target = "examTitle", source = "exam.title")
    @Mapping(target = "submittedAt", source = "createDate")
    ExamResultResponse toExamResultResponse(ExamAttempt attempt);
}


