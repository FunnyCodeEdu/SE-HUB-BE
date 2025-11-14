package com.se.hub.modules.interaction.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportResponse {
    String id;
    String reporterId;
    String reporterName;
    String reporterAvatar;
    String targetType;
    String targetId;
    String status;
    List<ReportReasonResponse> reasons;
    Instant createDate;
    Instant updatedDate;
    String createdBy;
    String updateBy;
}

