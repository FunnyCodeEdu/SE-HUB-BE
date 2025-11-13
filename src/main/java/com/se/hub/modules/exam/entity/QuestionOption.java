package com.se.hub.modules.exam.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.exam.constant.question_option.QuestionOptionConstants;
import com.se.hub.modules.exam.constant.question_option.QuestionOptionErrorCodeConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = QuestionOptionConstants.TABLE_QUESTION_OPTION)
@Entity
public class QuestionOption extends BaseEntity {
    @NotBlank(message = QuestionOptionErrorCodeConstants.QUESTION_OPTION_CONTENT_INVALID)
    @Size(min = QuestionOptionConstants.CONTENT_MIN_LENGTH,
            max = QuestionOptionConstants.CONTENT_MAX_LENGTH,
            message = QuestionOptionErrorCodeConstants.QUESTION_OPTION_CONTENT_INVALID)
    @Column(name = QuestionOptionConstants.COL_CONTENT,
            nullable = false,
            columnDefinition = QuestionOptionConstants.CONTENT_DEFINITION)
    String content;

    @Min(value = QuestionOptionConstants.ORDER_INDEX_MIN,
            message = QuestionOptionErrorCodeConstants.QUESTION_OPTION_ORDER_INDEX_INVALID)
    @Column(name = QuestionOptionConstants.COL_ORDER_INDEX,
            nullable = false)
    int orderIndex;

    @NotNull(message = QuestionOptionErrorCodeConstants.QUESTION_OPTION_IS_CORRECT_INVALID)
    @Column(name = QuestionOptionConstants.COL_IS_CORRECT,
            nullable = false)
    Boolean isCorrect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = QuestionOptionConstants.COL_QUESTION_ID,
            referencedColumnName = BaseFieldConstant.ID)
    Question question;
}



