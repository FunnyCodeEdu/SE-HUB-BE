package com.se.hub.modules.interaction.dto.request;

import com.se.hub.modules.interaction.constant.ReportConstants;
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

    @NotNull(message = "Report type is required")
    ReportType reportType;

    @Size(max = ReportConstants.DESCRIPTION_MAX_LENGTH,
            message = "Description max length is " + ReportConstants.DESCRIPTION_MAX_LENGTH)
    String description;
}

