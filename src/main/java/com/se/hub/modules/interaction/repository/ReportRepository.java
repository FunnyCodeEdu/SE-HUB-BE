package com.se.hub.modules.interaction.repository;

import com.se.hub.modules.interaction.entity.Report;
import com.se.hub.modules.interaction.enums.ReportStatus;
import com.se.hub.modules.interaction.enums.TargetType;
import com.se.hub.modules.profile.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, String> {

    /**
     * Find reports by target type and target ID
     */
    Page<Report> findByTargetTypeAndTargetId(TargetType targetType, String targetId, Pageable pageable);

    /**
     * Find reports by status
     */
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    /**
     * Find reports by reporter (Profile)
     */
    Page<Report> findByReporter(Profile reporter, Pageable pageable);

    /**
     * Find reports by reporter ID (using query)
     */
    @Query("""
        SELECT r FROM Report r
        WHERE r.reporter.id = :reporterId
        """)
    Page<Report> findByReporterId(@Param("reporterId") String reporterId, Pageable pageable);

    /**
     * Find reports by target type, target ID and status
     */
    Page<Report> findByTargetTypeAndTargetIdAndStatus(
            TargetType targetType, String targetId, ReportStatus status, Pageable pageable);

    /**
     * Count reports by target type and target ID
     */
    long countByTargetTypeAndTargetId(TargetType targetType, String targetId);

    /**
     * Count reports by status
     */
    long countByStatus(ReportStatus status);

    /**
     * Check if user has reported a target
     */
    @Query("""
        SELECT COUNT(r) > 0 FROM Report r
        WHERE r.reporter.id = :reporterId
        AND r.targetType = :targetType
        AND r.targetId = :targetId
        """)
    boolean existsByReporterIdAndTargetTypeAndTargetId(
            @Param("reporterId") String reporterId,
            @Param("targetType") TargetType targetType,
            @Param("targetId") String targetId);

    /**
     * Find report by reporter ID, target type and target ID
     */
    @Query("""
        SELECT r FROM Report r
        WHERE r.reporter.id = :reporterId
        AND r.targetType = :targetType
        AND r.targetId = :targetId
        """)
    Optional<Report> findByReporterIdAndTargetTypeAndTargetId(
            @Param("reporterId") String reporterId,
            @Param("targetType") TargetType targetType,
            @Param("targetId") String targetId);
}
