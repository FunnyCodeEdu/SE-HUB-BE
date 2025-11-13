package com.catsocute.japanlearn_hub.modules.exam.service.api;

import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.modules.exam.dto.request.SubmitExamRequest;
import com.catsocute.japanlearn_hub.modules.exam.dto.response.ExamResultResponse;

public interface ExamAttemptService {
    /**
     * Submit exam and calculate score
     * @author catsocute
     */
    ExamResultResponse submitExam(SubmitExamRequest request);
    
    /**
     * Get exam attempt history for a user
     * @author catsocute
     */
    PagingResponse<ExamResultResponse> getAttemptHistory(String userId, PagingRequest request);
    
    /**
     * Get exam attempt history for a specific exam
     * @author catsocute
     */
    PagingResponse<ExamResultResponse> getAttemptHistoryByExam(String examId, PagingRequest request);
    
    /**
     * Get attempt by ID
     * @author catsocute
     */
    ExamResultResponse getAttemptById(String attemptId);
    
    /**
     * Get all exam attempts with pagination
     * @author catsocute
     */
    PagingResponse<ExamResultResponse> getAllAttempts(PagingRequest request);
}

