package com.se.hub.modules.document.repository;

import com.se.hub.modules.document.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {
    /**
     * Get all approved documents by course ID with pagination
     */
    @Query("SELECT d FROM Document d WHERE d.course.id = :courseId AND d.isApproved = true")
    Page<Document> findAllByCourseIdAndApproved(@Param("courseId") String courseId, Pageable pageable);

    /**
     * Get all documents by course ID with pagination (including unapproved - for admin)
     */
    @Query("SELECT d FROM Document d WHERE d.course.id = :courseId")
    Page<Document> findAllByCourseId(@Param("courseId") String courseId, Pageable pageable);

    /**
     * Get top 4 most recent approved documents
     */
    List<Document> findTop4ByIsApprovedTrueOrderByCreateDateDesc();

    /**
     * Get all approved documents with pagination
     */
    @Query("SELECT d FROM Document d WHERE d.isApproved = true")
    Page<Document> findAllApproved(Pageable pageable);

    /**
     * Search approved documents by keyword (documentName or descript)
     */
    @Query("SELECT d FROM Document d WHERE d.isApproved = true AND " +
           "(LOWER(d.documentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.descript) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Document> searchApprovedByKeyword(@Param("keyword") String keyword);

    /**
     * Check if document exists and is approved
     */
    boolean existsByIdAndIsApprovedTrue(String id);
}

