package com.se.hub.modules.exam.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.exam.constant.ExamMessageConstants;
import com.se.hub.modules.exam.constant.exam_attempt.ExamAttemptConstants;
import com.se.hub.modules.profile.entity.Profile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@Table(name = ExamAttemptConstants.TABLE_EXAM_ATTEMPT)
@Entity
public class ExamAttempt extends BaseEntity {
    
    @NotNull(message = "Exam ID cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ExamAttemptConstants.COL_EXAM_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Exam exam;
    
    @NotNull(message = "Profile ID cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ExamAttemptConstants.COL_PROFILE_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Profile profile;
    
    @Min(value = 0, message = "Score cannot be negative")
    @Column(name = ExamAttemptConstants.COL_SCORE, nullable = false)
    int score;
    
    @Min(value = 0, message = "Total score cannot be negative")
    @Column(name = ExamAttemptConstants.COL_TOTAL_SCORE, nullable = false)
    int totalScore;
    
    @Column(name = ExamAttemptConstants.COL_CORRECT_COUNT)
    int correctCount;
    
    @Column(name = ExamAttemptConstants.COL_TOTAL_QUESTIONS)
    int totalQuestions;
    
    @Column(name = ExamAttemptConstants.COL_TIME_TAKEN_SECONDS)
    int timeTakenSeconds;
    
    @Column(name = ExamAttemptConstants.COL_SUBMITTED_ANSWERS, columnDefinition = ExamAttemptConstants.DESCRIPTION_DEFINITION)
    String submittedAnswers; // JSON string of questionId -> optionId mappings
}

