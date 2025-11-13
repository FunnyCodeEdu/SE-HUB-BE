package com.se.hub.modules.exam.repository;

import com.se.hub.modules.exam.entity.ExamAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, String> {
    Page<ExamAttempt> findByProfileIdOrderByCreateDateDesc(String profileId, Pageable pageable);
    
    Page<ExamAttempt> findByExamIdOrderByCreateDateDesc(String examId, Pageable pageable);
    
    List<ExamAttempt> findByExamIdAndProfileId(String examId, String profileId);
    
    long countByExamId(String examId);
    
    long countByExamIdAndProfileId(String examId, String profileId);
}

