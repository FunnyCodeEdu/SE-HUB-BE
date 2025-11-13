package com.catsocute.japanlearn_hub.modules.exam.dto.request;

import com.catsocute.japanlearn_hub.modules.exam.constant.question_option.QuestionOptionConstants;
import com.catsocute.japanlearn_hub.modules.exam.constant.question_option.QuestionOptionErrorCodeConstants;
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
public class CreateQuestionOptionRequest {
    
    @NotBlank(message = QuestionOptionErrorCodeConstants.QUESTION_OPTION_CONTENT_INVALID)
    @Size(min = QuestionOptionConstants.CONTENT_MIN_LENGTH,
            max = QuestionOptionConstants.CONTENT_MAX_LENGTH,
            message = QuestionOptionErrorCodeConstants.QUESTION_OPTION_CONTENT_INVALID)
    String content;

    @Min(value = QuestionOptionConstants.ORDER_INDEX_MIN,
            message = QuestionOptionErrorCodeConstants.QUESTION_OPTION_ORDER_INDEX_INVALID)
    int orderIndex;

    @NotNull(message = QuestionOptionErrorCodeConstants.QUESTION_OPTION_IS_CORRECT_INVALID)
    Boolean isCorrect;
}