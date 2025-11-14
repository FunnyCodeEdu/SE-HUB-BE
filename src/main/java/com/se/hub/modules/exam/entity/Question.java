package com.se.hub.modules.exam.entity;

import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.exam.constant.question.QuestionConstants;
import com.se.hub.modules.exam.constant.question.QuestionErrorCodeConstants;
import com.se.hub.modules.exam.enums.QuestionCategory;
import com.se.hub.modules.exam.enums.QuestionDifficulty;
import com.se.hub.modules.exam.enums.QuestionType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
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

import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = QuestionConstants.TABLE_QUESTION)
@Entity
public class Question extends BaseEntity {

    @NotBlank(message = QuestionErrorCodeConstants.QUESTION_CONTENT_INVALID)
    @Size(min = QuestionConstants.CONTENT_MIN_LENGTH,
            max = QuestionConstants.CONTENT_MAX_LENGTH,
            message = QuestionErrorCodeConstants.QUESTION_CONTENT_INVALID)
    @Column(name = QuestionConstants.COL_CONTENT,
            nullable = false,
            columnDefinition = QuestionConstants.CONTENT_DEFINITION)
    String content;

    @NotNull(message = QuestionErrorCodeConstants.QUESTION_TYPE_INVALID)
    @Enumerated(EnumType.STRING)
    @Column(name = QuestionConstants.COL_QUESTION_TYPE,
            nullable = false,
            columnDefinition = QuestionConstants.QUESTION_TYPE_DEFINITION)
    QuestionType questionType;

    @NotNull(message = QuestionErrorCodeConstants.QUESTION_DIFFICULTY_INVALID)
    @Enumerated(EnumType.STRING)
    @Column(name = QuestionConstants.COL_DIFFICULTY,
            nullable = false,
            columnDefinition = QuestionConstants.DIFFICULTY_DEFINITION)
    QuestionDifficulty difficulty;

    @Min(value = QuestionConstants.SCORE_MIN,
            message = QuestionErrorCodeConstants.QUESTION_SCORE_INVALID)
    @Max(value = QuestionConstants.SCORE_MAX,
            message = QuestionErrorCodeConstants.QUESTION_SCORE_INVALID)
    @Column(name = QuestionConstants.COL_SCORE,
            nullable = false)
    int score;

    @NotNull(message = QuestionErrorCodeConstants.QUESTION_CATEGORY_INVALID)
    @Enumerated(EnumType.STRING)
    @Column(name = QuestionConstants.COL_CATEGORY,
            nullable = false,
            columnDefinition = QuestionConstants.CATEGORY_DEFINITION)
    QuestionCategory category;

    @OneToMany(mappedBy = "question",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    List<QuestionOption> options;
}



