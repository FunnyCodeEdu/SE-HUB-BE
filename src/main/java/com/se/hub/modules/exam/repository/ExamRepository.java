package com.se.hub.modules.exam.repository;

import com.se.hub.modules.exam.entity.Exam;
import com.se.hub.modules.exam.repository.projection.ExamQuestionCountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, String> {
    boolean existsByExamCode(String examCode);
    Page<Exam> findAllByCourse_Id(String courseId, Pageable pageable);

    @Query("""
            SELECT e FROM Exam e
            WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(e.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Exam> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            SELECT e.id AS examId, COUNT(q.id) AS questionCount
            FROM Exam e
            LEFT JOIN e.questions q
            WHERE e.id IN :examIds
            GROUP BY e.id
            """)
    List<ExamQuestionCountProjection> findQuestionCountsByExamIds(@Param("examIds") Collection<String> examIds);
}
