package com.se.hub.modules.interaction.mapper;

import com.se.hub.modules.interaction.dto.request.CreateReportRequest;
import com.se.hub.modules.interaction.dto.response.ReportReasonResponse;
import com.se.hub.modules.interaction.dto.response.ReportResponse;
import com.se.hub.modules.interaction.entity.Report;
import com.se.hub.modules.interaction.entity.ReportReason;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    /**
     * Map CreateReportRequest -> Report entity
     * Note: reporter, status, reasons will be set in service layer
     */
    @Mapping(target = "reporter", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "reasons", ignore = true)
    @Mapping(target = "targetType", expression = "java(com.se.hub.modules.interaction.enums.TargetType.valueOf(request.getTargetType().toUpperCase()))")
    Report toReport(CreateReportRequest request);

    /**
     * Map Report entity -> ReportResponse
     */
    @Mapping(target = "reporterId", source = "reporter.id")
    @Mapping(target = "reporterName", source = "reporter.fullName")
    @Mapping(target = "reporterAvatar", source = "reporter.avtUrl")
    @Mapping(target = "targetType", expression = "java(report.getTargetType().name())")
    @Mapping(target = "status", expression = "java(report.getStatus().name())")
    @Mapping(target = "reasons", source = "reasons")
    ReportResponse toReportResponse(Report report);

    /**
     * Map ReportReason entity -> ReportReasonResponse
     */
    @Mapping(target = "reportType", expression = "java(reason.getReportType().name())")
    ReportReasonResponse toReportReasonResponse(ReportReason reason);

    /**
     * Map List<ReportReason> -> List<ReportReasonResponse>
     */
    List<ReportReasonResponse> toReportReasonResponseList(List<ReportReason> reasons);

    /**
     * Map List<Report> -> List<ReportResponse>
     */
    List<ReportResponse> toReportResponseList(List<Report> reports);
}

