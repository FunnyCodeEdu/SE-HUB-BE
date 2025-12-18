package com.se.hub.modules.interaction.dto.request;

import com.se.hub.modules.interaction.constant.ReportConstants;
import com.se.hub.modules.interaction.constant.ReportErrorCodeConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class CreateReportRequest {

    @NotBlank(message = ReportErrorCodeConstants.REPORT_TARGET_TYPE_IS_REQUIRED)
    @Pattern(regexp = "BLOG|QUESTION|COURSE|LESSON|COMMENT|EXAM|PRACTICAL_EXAM|DOCUMENT",
            message = ReportErrorCodeConstants.REPORT_INVALID_TARGET_TYPE)
    String targetType;

    @NotBlank(message = ReportErrorCodeConstants.REPORT_TARGET_TYPE_ID_IS_REQUIRED)
    @Size(max = ReportConstants.TARGET_ID_MAX_LENGTH,
            message = ReportErrorCodeConstants.REPORT_INVALID_TARGET_TYPE_ID_LENGTH)
    String targetId;

    @NotNull(message = ReportErrorCodeConstants.REPORT_REASON_IS_REQUIRED)
    @NotEmpty(message = ReportErrorCodeConstants.REPORT_REASON_IS_REQUIRED)
    @Valid
    List<ReportReasonRequest> reasons;
}

