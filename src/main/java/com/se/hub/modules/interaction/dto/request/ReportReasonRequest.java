package com.se.hub.modules.interaction.dto.request;

import com.se.hub.modules.interaction.constant.ReportConstants;
import com.se.hub.modules.interaction.constant.ReportErrorCodeConstants;
import com.se.hub.modules.interaction.enums.ReportType;
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
public class ReportReasonRequest {

    @NotNull(message = ReportErrorCodeConstants.REPORT_TYPE_IS_REQUIRED)
    ReportType reportType;

    @Size(max = ReportConstants.DESCRIPTION_MAX_LENGTH,
            message = ReportErrorCodeConstants.REPORT_INVALID_DESCRIPTION_LENGHT)
    String description;
}

