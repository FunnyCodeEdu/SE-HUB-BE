package com.se.hub.modules.exam.repository;

import com.se.hub.modules.exam.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
