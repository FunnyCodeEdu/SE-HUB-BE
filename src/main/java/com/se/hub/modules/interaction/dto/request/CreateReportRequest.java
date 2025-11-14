package com.se.hub.modules.interaction.dto.request;

import com.se.hub.modules.interaction.constant.ReportConstants;
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

    @NotBlank(message = "Target type is required")
    @Pattern(regexp = "BLOG|QUESTION|COURSE|LESSON|COMMENT|EXAM|PRACTICAL_EXAM|DOCUMENT",
            message = "Invalid target type")
    String targetType;

    @NotBlank(message = "Target ID is required")
    @Size(max = ReportConstants.TARGET_ID_MAX_LENGTH,
            message = "Target ID max length is " + ReportConstants.TARGET_ID_MAX_LENGTH)
    String targetId;

    @NotNull(message = "Report reasons are required")
    @NotEmpty(message = "At least one report reason is required")
    @Valid
    List<ReportReasonRequest> reasons;
}

