package com.catsocute.japanlearn_hub.modules.exam.service.api;

import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.modules.exam.dto.request.AddQuestionsToExamRequest;
import com.catsocute.japanlearn_hub.modules.exam.dto.request.CreateExamRequest;
import com.catsocute.japanlearn_hub.modules.exam.dto.request.RemoveQuestionsFromExamRequest;
import com.catsocute.japanlearn_hub.modules.exam.dto.request.UpdateExamRequest;
import com.catsocute.japanlearn_hub.modules.exam.dto.response.ExamResponse;

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
     * remove questions to exam
     * @author catsocute
     */
    ExamResponse removeQuestions(String examId, RemoveQuestionsFromExamRequest request);
}


