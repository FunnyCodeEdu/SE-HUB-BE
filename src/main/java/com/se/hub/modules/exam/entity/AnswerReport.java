package com.se.hub.modules.exam.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.exam.constant.answer_report.AnswerReportConstants;
import com.se.hub.modules.exam.enums.AnswerReportStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = AnswerReportConstants.TABLE_ANSWER_REPORT)
@Entity
public class AnswerReport extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AnswerReportConstants.COL_QUESTION_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AnswerReportConstants.COL_QUESTION_OPTION_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = true)
    QuestionOption questionOption;

    @NotNull
    @Column(name = AnswerReportConstants.COL_REPORTER_ID,
            nullable = false)
    String reporterId;

    @Size(max = AnswerReportConstants.SUGGESTED_CORRECT_ANSWER_MAX_LENGTH)
    @Column(name = AnswerReportConstants.COL_SUGGESTED_CORRECT_ANSWER,
            columnDefinition = AnswerReportConstants.SUGGESTED_CORRECT_ANSWER_DEFINITION)
    String suggestedCorrectAnswer;

    @Size(max = AnswerReportConstants.DESCRIPTION_MAX_LENGTH)
    @Column(name = AnswerReportConstants.COL_DESCRIPTION,
            columnDefinition = AnswerReportConstants.DESCRIPTION_DEFINITION)
    String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = AnswerReportConstants.COL_STATUS,
            nullable = false,
            columnDefinition = AnswerReportConstants.STATUS_DEFINITION)
    @Builder.Default
    AnswerReportStatus status = AnswerReportStatus.PENDING;

    @Column(name = AnswerReportConstants.COL_ADMIN_ID)
    String adminId; // Admin who processed the report
}

