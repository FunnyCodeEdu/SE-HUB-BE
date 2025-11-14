package com.se.hub.modules.exam.service;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.exam.dto.request.CreateQuestionRequest;
import com.se.hub.modules.exam.dto.request.UpdateQuestionRequest;
import com.se.hub.modules.exam.dto.response.QuestionResponse;
import com.se.hub.modules.exam.enums.QuestionCategory;
import com.se.hub.modules.exam.enums.QuestionDifficulty;
import com.se.hub.modules.exam.enums.QuestionType;

import java.util.List;

public interface QuestionService {
    /**
     * Create new question
     * @author catsocute
     */
    QuestionResponse createQuestion(CreateQuestionRequest request);

    /**
     * Get question by id
     * @author catsocute
     */
    QuestionResponse getById(String questionId);

    /**
     * Get all questions with pagination
     * @author catsocute
     */
    PagingResponse<QuestionResponse> getAllQuestions(PagingRequest request);

    /**
     * Get questions by category
     * @author catsocute
     */
    PagingResponse<QuestionResponse> getQuestionsByCategory(QuestionCategory category, PagingRequest request);

    /**
     * Get questions by difficulty
     * @author catsocute
     */
    PagingResponse<QuestionResponse> getQuestionsByDifficulty(QuestionDifficulty difficulty, PagingRequest request);

    /**
     * Get questions by question type
     * @author catsocute
     */
    PagingResponse<QuestionResponse> getQuestionsByType(QuestionType questionType, PagingRequest request);

    /**
     * Get questions by multiple criteria
     * @author catsocute
     */
    PagingResponse<QuestionResponse> getQuestionsByCriteria(QuestionCategory category,
                                                           QuestionDifficulty difficulty,
                                                           QuestionType questionType,
                                                           PagingRequest request);

    /**
     * Search questions by content
     * @author catsocute
     */
    PagingResponse<QuestionResponse> searchQuestionsByContent(String keyword, PagingRequest request);

    /**
     * Get questions by score range
     * @author catsocute
     */
    PagingResponse<QuestionResponse> getQuestionsByScoreRange(int minScore, int maxScore, PagingRequest request);

    /**
     * Get random questions
     * @author catsocute
     */
    List<QuestionResponse> getRandomQuestions(QuestionCategory category,
                                             QuestionDifficulty difficulty,
                                             QuestionType questionType,
                                             int limit);

    /**
     * Update question by id
     * @author catsocute
     */
    QuestionResponse updateQuestion(String questionId, UpdateQuestionRequest request);

    /**
     * Delete question by id
     * @author catsocute
     */
    void deleteQuestion(String questionId);
}

