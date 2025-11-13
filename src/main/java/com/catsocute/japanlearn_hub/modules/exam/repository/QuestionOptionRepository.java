package com.catsocute.japanlearn_hub.modules.exam.repository;

import com.catsocute.japanlearn_hub.modules.exam.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, String> {
    
    /**
     * Find all options for a specific question
     */
    List<QuestionOption> findByQuestionIdOrderByOrderIndex(String questionId);
    
    /**
     * Find all options for a specific question
     */
    List<QuestionOption> findByQuestionId(String questionId);
    
    /**
     * Find correct options for a specific question
     */
    List<QuestionOption> findByQuestionIdAndIsCorrectTrue(String questionId);
    
    /**
     * Find incorrect options for a specific question
     */
    List<QuestionOption> findByQuestionIdAndIsCorrectFalse(String questionId);
    
    /**
     * Count options for a specific question
     */
    long countByQuestionId(String questionId);
    
    /**
     * Count correct options for a specific question
     */
    long countByQuestionIdAndIsCorrectTrue(String questionId);
    
    /**
     * Delete all options for a specific question
     */
    void deleteByQuestionId(String questionId);
    
    /**
     * Find option by question ID and order index
     */
    Optional<QuestionOption> findByQuestionIdAndOrderIndex(String questionId, int orderIndex);
    
    /**
     * Find options by content containing keyword
     */
    @Query("SELECT qo FROM QuestionOption qo WHERE qo.content LIKE %:keyword%")
    List<QuestionOption> findByContentContaining(@Param("keyword") String keyword);
}