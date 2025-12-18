package com.se.hub.modules.interaction.dto.request;

import com.se.hub.modules.interaction.constant.ReportErrorCodeConstants;
import com.se.hub.modules.interaction.enums.ReportStatus;
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
public class UpdateReportRequest {

    @NotNull(message = ReportErrorCodeConstants.REPORT_STATUS_IS_REQUIRED)
    ReportStatus status;
}

