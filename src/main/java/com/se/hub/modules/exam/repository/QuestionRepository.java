package com.se.hub.modules.exam.repository;

import com.se.hub.modules.exam.entity.Question;
import com.se.hub.modules.exam.enums.QuestionCategory;
import com.se.hub.modules.exam.enums.QuestionDifficulty;
import com.se.hub.modules.exam.enums.QuestionType;
import com.se.hub.modules.lesson.enums.JLPTLevel;
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
     * Note: This query uses PostgreSQL RANDOM() function and named parameters
     */
    @Query(value = """
        SELECT q.* FROM question q
        WHERE (:category IS NULL OR q.category = :category)
            AND (:difficulty IS NULL OR q.difficulty = :difficulty)
            AND (:jlptLevel IS NULL OR q.jlpt_level = :jlptLevel)
            AND (:questionType IS NULL OR q.question_type = :questionType)
        ORDER BY RANDOM()
        LIMIT :limit
        """, nativeQuery = true)
    List<Question> findRandomQuestions(@Param("category") String category,
                                     @Param("difficulty") String difficulty,
                                     @Param("jlptLevel") String jlptLevel,
                                     @Param("questionType") String questionType,
                                     @Param("limit") int limit);
}