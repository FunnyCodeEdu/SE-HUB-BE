package com.se.hub.modules.lesson.dto.request;

import com.se.hub.modules.lesson.constant.lesson.LessonConstants;
import com.se.hub.modules.lesson.constant.lesson.LessonErrorCodeConstants;
import com.se.hub.modules.lesson.enums.LessonType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateLessonRequest {
    @NotBlank(message = LessonErrorCodeConstants.LESSON_TITLE_INVALID)
    @NotNull(message = LessonErrorCodeConstants.LESSON_TITLE_INVALID)
    @Size(min = LessonConstants.TITLE_MIN_LENGTH,
          max = LessonConstants.TITLE_MAX_LENGTH,
          message = LessonErrorCodeConstants.LESSON_TITLE_INVALID)
    String title;

    @NotNull(message = LessonErrorCodeConstants.LESSON_TYPE_INVALID)
    @Enumerated(EnumType.STRING)
    LessonType type;

    @Size(max = LessonConstants.DESCRIPTION_MAX_LENGTH,
          message = LessonErrorCodeConstants.LESSON_DESCRIPTION_INVALID)
    String description;

    String parentLessonId;
}