package com.catsocute.japanlearn_hub.modules.exam.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamResultResponse {
    
    String attemptId;
    String examId;
    String examTitle;
    
    int score;
    int totalScore;
    double percentage;
    
    int correctCount;
    int totalQuestions;
    
    int timeTakenSeconds;
    
    /**
     * Detailed breakdown of each question
     */
    List<QuestionResultDetail> questionResults;
    
    Instant submittedAt;
    
    /**
     * Profile information of the user who took the exam
     */
    ProfileInfo profile;
    
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class QuestionResultDetail {
        String questionId;
        String questionContent;
        String selectedOptionId;
        String correctOptionId;
        boolean isCorrect;
        int pointsEarned;
        int pointsTotal;
    }
    
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ProfileInfo {
        String id;
        String displayName;
        String username;
        String avtUrl;
    }
}

