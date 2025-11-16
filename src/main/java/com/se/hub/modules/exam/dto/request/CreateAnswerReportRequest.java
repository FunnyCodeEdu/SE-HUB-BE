package com.se.hub.modules.exam.dto.request;

import com.se.hub.modules.exam.constant.answer_report.AnswerReportConstants;
import com.se.hub.modules.exam.constant.answer_report.AnswerReportErrorCodeConstants;
import jakarta.validation.constraints.NotBlank;
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
public class CreateAnswerReportRequest {

    @NotBlank(message = AnswerReportErrorCodeConstants.ANSWER_REPORT_QUESTION_ID_INVALID)
    String questionId;

    @NotBlank(message = AnswerReportErrorCodeConstants.ANSWER_REPORT_QUESTION_OPTION_ID_INVALID)
    String questionOptionId;

    @Size(max = AnswerReportConstants.SUGGESTED_CORRECT_ANSWER_MAX_LENGTH,
            message = AnswerReportErrorCodeConstants.ANSWER_REPORT_SUGGESTED_ANSWER_INVALID)
    String suggestedCorrectAnswer;

    @Size(max = AnswerReportConstants.DESCRIPTION_MAX_LENGTH,
            message = AnswerReportErrorCodeConstants.ANSWER_REPORT_DESCRIPTION_INVALID)
    String description;
}

