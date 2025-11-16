package com.se.hub.modules.exam.repository;

import com.se.hub.modules.exam.entity.AnswerReport;
import com.se.hub.modules.exam.enums.AnswerReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerReportRepository extends JpaRepository<AnswerReport, String> {

    /**
     * Find reports by status
     */
    Page<AnswerReport> findByStatus(AnswerReportStatus status, Pageable pageable);

    /**
     * Find reports by reporter ID
     */
    Page<AnswerReport> findByReporterId(String reporterId, Pageable pageable);

    /**
     * Find reports by question ID
     */
    Page<AnswerReport> findByQuestionId(String questionId, Pageable pageable);

    /**
     * Find reports by question option ID
     */
    Page<AnswerReport> findByQuestionOptionId(String questionOptionId, Pageable pageable);

    /**
     * Delete all reports by question ID
     */
    void deleteByQuestionId(String questionId);

    /**
     * Delete all reports by question option ID
     */
    void deleteByQuestionOptionId(String questionOptionId);
}

