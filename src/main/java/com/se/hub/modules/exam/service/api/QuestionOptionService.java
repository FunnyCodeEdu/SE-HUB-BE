package com.se.hub.modules.exam.service.api;

import com.se.hub.modules.exam.dto.request.CreateQuestionOptionRequest;
import com.se.hub.modules.exam.dto.request.UpdateQuestionOptionRequest;
import com.se.hub.modules.exam.dto.response.QuestionOptionResponse;
import com.se.hub.modules.exam.entity.Question;

import java.util.List;

public interface QuestionOptionService {
    /**
     * Create new question option
     * @author catsocute
     */
    QuestionOptionResponse createQuestionOption(Question question, CreateQuestionOptionRequest request);

    /**
     * Get question option by id
     * @author catsocute
     */
    QuestionOptionResponse getById(String optionId);

    /**
     * Get all options for a question
     * @author catsocute
     */
    List<QuestionOptionResponse> getOptionsByQuestionId(String questionId);

    /**
     * Get correct options for a question
     * @author catsocute
     */
    List<QuestionOptionResponse> getCorrectOptionsByQuestionId(String questionId);

    /**
     * Update question option by id
     * @author catsocute
     */
    QuestionOptionResponse updateQuestionOption(String optionId, UpdateQuestionOptionRequest request);

    /**
     * Delete question option by id
     * @author catsocute
     */
    void deleteQuestionOption(String optionId);

    /**
     * Delete all options for a question
     * @author catsocute
     */
    void deleteOptionsByQuestionId(String questionId);
}
