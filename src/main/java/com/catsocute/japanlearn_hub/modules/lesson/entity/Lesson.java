package com.catsocute.japanlearn_hub.modules.lesson.entity;

import com.catsocute.japanlearn_hub.common.constant.BaseFieldConstant;
import com.catsocute.japanlearn_hub.common.entity.BaseEntity;
import com.catsocute.japanlearn_hub.modules.course.entity.Course;
import com.catsocute.japanlearn_hub.modules.lesson.constant.lesson.LessonConstants;
import com.catsocute.japanlearn_hub.modules.lesson.constant.lesson.LessonErrorCodeConstants;
import com.catsocute.japanlearn_hub.modules.lesson.enums.LessonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = LessonConstants.TABLE_LESSON)
@Entity
public class Lesson extends BaseEntity {
    @NotBlank(message = LessonErrorCodeConstants.LESSON_TITLE_INVALID)
    @NotNull(message = LessonErrorCodeConstants.LESSON_TITLE_INVALID)
    @Size(min = LessonConstants.TITLE_MIN_LENGTH,
            max = LessonConstants.TITLE_MAX_LENGTH,
            message = LessonErrorCodeConstants.LESSON_TITLE_INVALID)
    @Column(name = LessonConstants.COL_TITLE,
            nullable = false,
            columnDefinition = LessonConstants.TITLE_DEFINITION)
    String title;

    @NotNull(message = LessonErrorCodeConstants.LESSON_TYPE_INVALID)
    @Enumerated(EnumType.STRING)
    @Column(name = LessonConstants.COL_TYPE,
            nullable = false,
            columnDefinition = LessonConstants.TYPE_DEFINITION)
    LessonType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = LessonConstants.COL_PARENT_LESSON,
            referencedColumnName = BaseFieldConstant.ID)
    Lesson parentLesson;

    @Size(max = LessonConstants.DESCRIPTION_MAX_LENGTH,
            message = LessonErrorCodeConstants.LESSON_DESCRIPTION_INVALID)
    @Column(name = LessonConstants.COL_DESCRIPTION,
            columnDefinition = LessonConstants.DESCRIPTION_DEFINITION)
    String description;

    @ManyToMany(fetch = FetchType.LAZY)
    Set<Grammar> grammars;

    @ManyToMany(fetch = FetchType.LAZY)
    Set<Vocabulary> vocabularies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = LessonConstants.COL_COURSE_ID,
            referencedColumnName = BaseFieldConstant.ID)
    Course course;
}
