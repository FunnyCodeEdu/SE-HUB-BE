package com.se.hub.modules.exam.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.course.entity.Course;
import com.se.hub.modules.exam.constant.exam.ExamConstants;
import com.se.hub.modules.exam.constant.exam.ExamErrorCodeConstants;
import com.se.hub.modules.exam.enums.ExamType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
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
@Table(name = ExamConstants.TABLE_EXAM)
@Entity
public class Exam extends BaseEntity {
    @NotBlank(message = ExamErrorCodeConstants.EXAM_TITLE_INVALID)
    @Size(min = ExamConstants.TITLE_MIN_LENGTH,
            max = ExamConstants.TITLE_MAX_LENGTH,
            message = ExamErrorCodeConstants.EXAM_TITLE_INVALID)
    @Column(name = ExamConstants.COL_TITLE,
            nullable = false,
            columnDefinition = ExamConstants.TITLE_DEFINITION)
    String title;

    @Size(max = ExamConstants.DESCRIPTION_MAX_LENGTH,
            message = ExamErrorCodeConstants.EXAM_DESCRIPTION_INVALID)
    @Column(name = ExamConstants.COL_DESCRIPTION,
            columnDefinition = ExamConstants.DESCRIPTION_DEFINITION)
    String description;

    @Min(value = ExamConstants.DURATION_MIN,
            message = ExamErrorCodeConstants.EXAM_DURATION_INVALID)
    @Column(name = ExamConstants.COL_DURATION_MINUTES,
            nullable = false)
    int durationMinutes;

    @NotNull(message = ExamErrorCodeConstants.EXAM_TYPE_INVALID)
    @Enumerated(EnumType.STRING)
    @Column(name = ExamConstants.COL_EXAM_TYPE,
            nullable = false,
            columnDefinition = ExamConstants.EXAM_TYPE_DEFINITION)
    ExamType examType;

    @NotBlank(message = ExamErrorCodeConstants.EXAM_CODE_INVALID)
    @Size(min = ExamConstants.EXAM_CODE_MIN_LENGTH,
            max = ExamConstants.EXAM_CODE_MAX_LENGTH,
            message = ExamErrorCodeConstants.EXAM_CODE_INVALID)
    @Column(name = ExamConstants.COL_EXAM_CODE,
            unique = true,
            nullable = false,
            columnDefinition = ExamConstants.EXAM_CODE_DEFINITION)
    String examCode;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = ExamConstants.COL_COURSE_ID,
            referencedColumnName = BaseFieldConstant.ID)
    Course course;

    @ManyToMany(fetch = FetchType.LAZY)
    Set<Question> questions;
}


