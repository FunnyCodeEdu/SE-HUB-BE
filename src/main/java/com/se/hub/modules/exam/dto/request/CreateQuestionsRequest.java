package com.se.hub.modules.exam.dto.request;

import com.se.hub.modules.exam.constant.QuestionMessageConstants;
import com.se.hub.modules.exam.constant.question.QuestionErrorCodeConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
public class CreateQuestionsRequest {
    @NotEmpty(message = QuestionMessageConstants.QUESTION_LIST_CAN_NOT_BE_EMPTY)
    @Valid
    List<CreateQuestionRequest> questions;
}

