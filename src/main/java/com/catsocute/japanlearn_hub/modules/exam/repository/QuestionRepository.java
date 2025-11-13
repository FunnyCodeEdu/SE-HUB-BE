package com.catsocute.japanlearn_hub.modules.exam.repository;

import com.catsocute.japanlearn_hub.modules.exam.entity.Question;
import com.catsocute.japanlearn_hub.modules.exam.enums.QuestionCategory;
import com.catsocute.japanlearn_hub.modules.exam.enums.QuestionDifficulty;
import com.catsocute.japanlearn_hub.modules.exam.enums.QuestionType;
import com.catsocute.japanlearn_hub.modules.lesson.enums.JLPTLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
    
    /**
     * Find questions by category
     */
    Page<Question> findByCategory(QuestionCategory category, Pageable pageable);
    
    /**
     * Find questions by difficulty
     */
    Page<Question> findByDifficulty(QuestionDifficulty difficulty, Pageable pageable);
    
    /**
     * Find questions by JLPT level
     */
    Page<Question> findByJlptLevel(JLPTLevel jlptLevel, Pageable pageable);
    
    /**
     * Find questions by question type
     */
    Page<Question> findByQuestionType(QuestionType questionType, Pageable pageable);
    
    /**
     * Find questions by multiple criteria
     */
    @Query("SELECT q FROM Question q WHERE " +
           "(:category IS NULL OR q.category = :category) AND " +
           "(:difficulty IS NULL OR q.difficulty = :difficulty) AND " +
           "(:jlptLevel IS NULL OR q.jlptLevel = :jlptLevel) AND " +
           "(:questionType IS NULL OR q.questionType = :questionType)")
    Page<Question> findByCriteria(@Param("category") QuestionCategory category,
                                 @Param("difficulty") QuestionDifficulty difficulty,
                                 @Param("jlptLevel") JLPTLevel jlptLevel,
                                 @Param("questionType") QuestionType questionType,
                                 Pageable pageable);
    
    /**
     * Find questions by content containing keyword
     */
    Page<Question> findByContentContainingIgnoreCase(String keyword, Pageable pageable);
    
    /**
     * Find questions by score range
     */
    Page<Question> findByScoreBetween(int minScore, int maxScore, Pageable pageable);
    
    /**
     * Find random questions by criteria
     * Note: This query uses a workaround for MySQL LIMIT with parameter binding
     */
    @Query(value = """
        SELECT q.* FROM question q
        WHERE (?1 IS NULL OR q.category = ?1)
            AND (?2 IS NULL OR q.difficulty = ?2)
            AND (?3 IS NULL OR q.jlpt_level = ?3)
            AND (?4 IS NULL OR q.question_type = ?4)
        ORDER BY RAND()
        LIMIT ?5
        """, nativeQuery = true)
    List<Question> findRandomQuestions(String category,
                                     String difficulty,
                                     String jlptLevel,
                                     String questionType,
                                     int limit);
}