package com.catsocute.japanlearn_hub.modules.lesson.mapper;

import com.catsocute.japanlearn_hub.modules.lesson.dto.request.CreateLessonRequest;
import com.catsocute.japanlearn_hub.modules.lesson.dto.request.UpdateLessonRequest;
import com.catsocute.japanlearn_hub.modules.lesson.dto.response.LessonResponse;
import com.catsocute.japanlearn_hub.modules.lesson.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {GrammarMapper.class, VocabularyMapper.class})
public interface LessonMapper {
    @Mapping(target = "parentLesson", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "grammars", ignore = true)
    @Mapping(target = "vocabularies", ignore = true)
    Lesson toLesson(CreateLessonRequest request);
    
    @Mapping(target = "parentLessonId", source = "parentLesson.id")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "grammars", source = "grammars")
    @Mapping(target = "vocabularies", source = "vocabularies")
    LessonResponse toLessonResponse(Lesson lesson);
    
    @Mapping(target = "parentLesson", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "grammars", ignore = true)
    @Mapping(target = "vocabularies", ignore = true)
    void updateLessonFromRequest(@MappingTarget Lesson lesson, UpdateLessonRequest request);
}
