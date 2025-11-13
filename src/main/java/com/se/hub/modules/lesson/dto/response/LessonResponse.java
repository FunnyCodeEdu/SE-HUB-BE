package com.se.hub.modules.lesson.dto.response;

import com.se.hub.modules.lesson.enums.LessonType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Set;

/**
 * Lesson response DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonResponse {
    String id;
    String title;
    LessonType type;
    String description;
    String parentLessonId;
    String courseId;
    Set<GrammarResponse> grammars;
    Set<VocabularyResponse> vocabularies;
    Instant createDate;
    Instant updatedDate;
    String createdBy;
    String updateBy;
}