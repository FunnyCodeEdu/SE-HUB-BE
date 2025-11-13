package com.se.hub.modules.exam.mapper;

import com.se.hub.modules.exam.dto.request.CreateExamRequest;
import com.se.hub.modules.exam.dto.request.UpdateExamRequest;
import com.se.hub.modules.exam.dto.response.ExamResponse;
import com.se.hub.modules.exam.entity.Exam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {QuestionMapper.class})
public interface ExamMapper {
    Exam toExam(CreateExamRequest request);
    
    @Mapping(target = "courseId", source = "course.id")
    ExamResponse toExamResponse(Exam exam);

    void updateExamFromRequest(@MappingTarget Exam exam, UpdateExamRequest request);
}
