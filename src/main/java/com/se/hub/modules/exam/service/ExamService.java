package com.se.hub.modules.exam.service;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.exam.dto.request.AddQuestionsToExamRequest;
import com.se.hub.modules.exam.dto.request.CreateExamRequest;
import com.se.hub.modules.exam.dto.request.CreateQuestionRequest;
import com.se.hub.modules.exam.dto.request.RemoveQuestionsFromExamRequest;
import com.se.hub.modules.exam.dto.request.UpdateExamRequest;
import com.se.hub.modules.exam.dto.response.ExamResponse;

import java.util.List;

public interface ExamService {
    /**
     * Create new exam
     * @author catsocute
     */
    ExamResponse create(CreateExamRequest request);

    /**
     * get exam by id
     * @author catsocute
     */
    ExamResponse getById(String examId);

    /**
     * get all exams
     * @author catsocute
     */
    PagingResponse<ExamResponse> getAll(PagingRequest request);

    /**
     * get exams by course id
     * @author catsocute
     */
    PagingResponse<ExamResponse> getByCourseId(String courseId, PagingRequest request);

    /**
     * update exam by id
     * @author catsocute
     */
    ExamResponse updateById(String examId, UpdateExamRequest request);

    /**
     * delete by id
     * @author catsocute
     */
    void deleteById(String examId);

    // Dedicated question mgmt APIs
    /**
     * add questions to exam
     * @author catsocute
     */
    ExamResponse addQuestions(String examId, AddQuestionsToExamRequest request);

    /**
     * remove questions from exam
     * @author catsocute
     */
    ExamResponse removeQuestions(String examId, RemoveQuestionsFromExamRequest request);
    
    /**
     * Create questions and add them to an exam
     * @param examId Exam ID
     * @param requests List of question requests
     * @return ExamResponse with updated questions
     */
    ExamResponse createQuestionsForExam(String examId, List<CreateQuestionRequest> requests);
    
    /**
     * Create exam with questions in one transaction
     * @param examRequest Exam request
     * @param questionRequests List of question requests
     * @return ExamResponse with questions
     */
    ExamResponse createExamWithQuestions(CreateExamRequest examRequest, List<CreateQuestionRequest> questionRequests);
}

