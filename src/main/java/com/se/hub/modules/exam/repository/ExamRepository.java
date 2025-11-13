package com.se.hub.modules.exam.repository;

import com.se.hub.modules.exam.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, String> {
    boolean existsByExamCode(String examCode);
    Page<Exam> findAllByCourse_Id(String courseId, Pageable pageable);
}
