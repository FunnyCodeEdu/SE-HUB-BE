package com.se.hub.modules.exam.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
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
@Table(name = "exam_attempt")
@Entity
public class ExamAttempt extends BaseEntity {
    
    @NotNull(message = "Exam ID cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", 
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Exam exam;
    
    @NotNull(message = "Profile ID cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", 
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Profile profile;
    
    @Min(value = 0, message = "Score cannot be negative")
    @Column(name = "score", nullable = false)
    int score;
    
    @Min(value = 0, message = "Total score cannot be negative")
    @Column(name = "total_score", nullable = false)
    int totalScore;
    
    @Column(name = "correct_count")
    int correctCount;
    
    @Column(name = "total_questions")
    int totalQuestions;
    
    @Column(name = "time_taken_seconds")
    int timeTakenSeconds;
    
    @Column(name = "submitted_answers", columnDefinition = "TEXT")
    String submittedAnswers; // JSON string of questionId -> optionId mappings
}

