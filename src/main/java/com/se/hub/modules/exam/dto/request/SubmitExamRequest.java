package com.se.hub.modules.exam.dto.request;

import com.se.hub.modules.exam.constant.AnswerReportMessageConstants;
import com.se.hub.modules.exam.constant.ExamMessageConstants;
import com.se.hub.modules.exam.constant.answer_report.AnswerReportErrorCodeConstants;
import com.se.hub.modules.exam.constant.exam.ExamErrorCodeConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmitExamRequest {
    
    @NotNull(message = ExamMessageConstants.EXAM_ID_CAN_NOT_BE_NULL)
    private String examId;
    
    /**
     * Map of question ID to selected option ID(s)
     * For MULTIPLE_CHOICE and TRUE_FALSE: single option ID
     * For FILL_IN_BLANK: the option ID or null if not answered
     */
    @NotEmpty(message = AnswerReportErrorCodeConstants.ANSWER_REPORT_CAN_NOT_BE_EMPTY)
    @Valid
    private Map<String, String> answers;
    
    /**
     * Time taken in seconds
     */
    @NotNull(message = ExamMessageConstants.EXAM_TIME_TAKEN_CANNOT_BE_NULL)
    private Integer timeTakenSeconds;
}


