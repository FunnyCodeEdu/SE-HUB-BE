package com.se.hub.modules.interaction.dto.response;

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
public class ReportSummaryResponse {
    long totalReports;
    long pendingReports;
    long approvedReports;
    long rejectedReports;
    long resolvedReports;
    Map<String, Long> reportsByType; // Key: ReportType name, Value: count
}

