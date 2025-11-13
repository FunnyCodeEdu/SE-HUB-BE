package com.se.hub.modules.exam.dto.request;

import com.se.hub.modules.exam.constant.question.QuestionConstants;
import com.se.hub.modules.exam.constant.question.QuestionErrorCodeConstants;
import com.se.hub.modules.exam.enums.QuestionCategory;
import com.se.hub.modules.exam.enums.QuestionDifficulty;
import com.se.hub.modules.exam.enums.QuestionType;
import com.se.hub.modules.lesson.enums.JLPTLevel;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
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
public class CreateQuestionRequest {
    
    @NotBlank(message = QuestionErrorCodeConstants.QUESTION_CONTENT_INVALID)
    @Size(min = QuestionConstants.CONTENT_MIN_LENGTH,
            max = QuestionConstants.CONTENT_MAX_LENGTH,
            message = QuestionErrorCodeConstants.QUESTION_CONTENT_INVALID)
    String content;

    @NotNull(message = QuestionErrorCodeConstants.QUESTION_TYPE_INVALID)
    @Enumerated(EnumType.STRING)
    QuestionType questionType;

    @NotNull(message = QuestionErrorCodeConstants.QUESTION_DIFFICULTY_INVALID)
    @Enumerated(EnumType.STRING)
    QuestionDifficulty difficulty;

    @Min(value = QuestionConstants.SCORE_MIN,
            message = QuestionErrorCodeConstants.QUESTION_SCORE_INVALID)
    @Max(value = QuestionConstants.SCORE_MAX,
            message = QuestionErrorCodeConstants.QUESTION_SCORE_INVALID)
    int score;

    @NotNull(message = QuestionErrorCodeConstants.QUESTION_CATEGORY_INVALID)
    @Enumerated(EnumType.STRING)
    QuestionCategory category;

    @NotNull(message = QuestionErrorCodeConstants.QUESTION_JLPT_LEVEL_INVALID)
    @Enumerated(EnumType.STRING)
    JLPTLevel jlptLevel;

    @Valid
    List<CreateQuestionOptionRequest> options;
}