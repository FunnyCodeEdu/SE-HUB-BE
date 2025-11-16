package com.se.hub.modules.exam.dto.request;

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
    
    @NotNull(message = "Status is required")
    AnswerReportStatus status;
}

