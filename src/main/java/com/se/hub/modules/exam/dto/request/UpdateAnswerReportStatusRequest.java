package com.se.hub.modules.exam.dto.request;

import com.se.hub.modules.exam.constant.AnswerReportMessageConstants;
import com.se.hub.modules.exam.constant.answer_report.AnswerReportErrorCodeConstants;
import com.se.hub.modules.exam.enums.AnswerReportStatus;
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
public class UpdateAnswerReportStatusRequest {
    
    @NotNull(message = AnswerReportErrorCodeConstants.ANSWER_REPORT_STATUS_IS_REQUIRED)
    AnswerReportStatus status;
}

