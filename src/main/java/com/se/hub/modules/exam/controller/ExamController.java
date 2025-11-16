package com.se.hub.modules.exam.controller;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.constant.PaginationConstants;
import com.se.hub.common.constant.ResponseCode;
import com.se.hub.common.controller.BaseController;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.request.SortRequest;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.exam.constant.ExamMessageConstants;
import com.se.hub.modules.exam.dto.request.AddQuestionsToExamRequest;
import com.se.hub.modules.exam.dto.request.CreateExamRequest;
import com.se.hub.modules.exam.dto.request.CreateExamWithQuestionsRequest;
import com.se.hub.modules.exam.dto.request.CreateQuestionsRequest;
import com.se.hub.modules.exam.dto.request.RemoveQuestionsFromExamRequest;
import com.se.hub.modules.exam.dto.request.SubmitExamRequest;
import com.se.hub.modules.exam.dto.request.UpdateExamRequest;
import com.se.hub.modules.exam.dto.response.ExamResponse;
import com.se.hub.modules.exam.dto.response.ExamResultResponse;
import com.se.hub.modules.exam.dto.response.QuestionResponse;
import com.se.hub.modules.exam.service.ExamAttemptService;
import com.se.hub.modules.exam.service.ExamService;
import com.se.hub.modules.profile.repository.ProfileRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Exam Management",
        description = "Exam management API")
@RequestMapping("/exams")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class ExamController extends BaseController {
    ExamService examService;
    ExamAttemptService examAttemptService;
    ProfileRepository profileRepository;

    @PostMapping
    @Operation(summary = "Create new exam",
            description = "Create a new exam in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ExamMessageConstants.API_EXAM_CREATED_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ExamMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ExamMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<ExamResponse>> create(@Valid @RequestBody CreateExamRequest request) {
        ExamResponse examResponse = examService.create(request);
        return success(examResponse, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all exams",
            description = "Get list of all exams with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ExamMessageConstants.API_EXAM_RETRIEVED_ALL_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ExamMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ExamMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ExamResponse>>> getAll(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        return success(examService.getAll(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get exams by course ID",
            description = "Get list of exams for a specific course with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ExamMessageConstants.API_EXAM_RETRIEVED_BY_COURSE_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ExamMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ExamMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ExamResponse>>> getByCourseId(
            @PathVariable String courseId,
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        return success(examService.getByCourseId(courseId, request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/{examId}")
    @Operation(summary = "Get exam by ID",
            description = "Get exam information by exam ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ExamMessageConstants.API_EXAM_RETRIEVED_BY_ID_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ExamMessageConstants.EXAM_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ExamMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<ExamResponse>> getById(@PathVariable String examId) {
        return success(examService.getById(examId), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/{examId}/questions")
    @Operation(summary = "Get questions by exam ID",
            description = "Get list of questions for a specific exam")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = "Questions retrieved successfully"),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ExamMessageConstants.EXAM_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ExamMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<List<QuestionResponse>>> getQuestionsByExamId(@PathVariable String examId) {
        return success(examService.getQuestionsByExamId(examId), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @PutMapping("/{examId}")
    @Operation(summary = "Update exam",
            description = "Update exam information by exam ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ExamMessageConstants.API_EXAM_UPDATED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ExamMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ExamMessageConstants.EXAM_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ExamMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<ExamResponse>> update(
            @PathVariable String examId,
            @Valid @RequestBody UpdateExamRequest request) {
        return success(examService.updateById(examId, request), MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @DeleteMapping("/{examId}")
    @Operation(summary = "Delete exam",
            description = "Delete a exam from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ExamMessageConstants.API_EXAM_DELETED_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ExamMessageConstants.EXAM_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ExamMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<Void>> delete(@PathVariable String examId) {
        examService.deleteById(examId);
        return success(null, MessageCodeConstant.M004_DELETED, MessageConstant.DELETED);
    }

    // Dedicated question endpoints
    @PostMapping("/{examId}/questions:add")
    @Operation(summary = "Add questions to exam",
            description = "Add questions to exam by a list of questionIds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ExamMessageConstants.API_EXAM_QUESTIONS_ADDED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ExamMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ExamMessageConstants.EXAM_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ExamMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<ExamResponse>> addQuestions(
            @PathVariable String examId,
            @Valid @RequestBody AddQuestionsToExamRequest request) {
        return success(examService.addQuestions(examId, request), MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @PostMapping("/{examId}/questions:remove")
    @Operation(summary = "Remove questions from exam",
            description = "Remove questions from exam by a list of questionIds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ExamMessageConstants.API_EXAM_QUESTIONS_REMOVED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ExamMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ExamMessageConstants.EXAM_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ExamMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<ExamResponse>> removeQuestions(
            @PathVariable String examId,
            @Valid @RequestBody RemoveQuestionsFromExamRequest request) {
        return success(examService.removeQuestions(examId, request), MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @PostMapping("/{examId}/questions:create")
    @Operation(summary = "Create questions and add to exam",
            description = "Create multiple questions with duplicate checking and add them to an exam")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = "Questions created and added to exam successfully"),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ExamMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ExamMessageConstants.EXAM_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ExamMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<ExamResponse>> createQuestionsForExam(
            @PathVariable String examId,
            @Valid @RequestBody CreateQuestionsRequest request) {
        return success(examService.createQuestionsForExam(examId, request.getQuestions()), 
                MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }

    @PostMapping("/with-questions")
    @Operation(summary = "Create exam with questions",
            description = "Create an exam and multiple questions in one transaction with duplicate checking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = "Exam and questions created successfully"),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ExamMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ExamMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<ExamResponse>> createExamWithQuestions(
            @Valid @RequestBody CreateExamWithQuestionsRequest request) {
        return success(examService.createExamWithQuestions(request.getExam(), request.getQuestions()), 
                MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }

    // Exam submission and scoring endpoints
    @PostMapping("/submit")
    @Operation(summary = "Submit exam and get results",
            description = "Submit exam answers and receive detailed scoring results")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ExamMessageConstants.API_EXAM_SUBMITTED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ExamMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ExamMessageConstants.EXAM_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ExamMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<ExamResultResponse>> submitExam(
            @Valid @RequestBody SubmitExamRequest request) {
        return success(examAttemptService.submitExam(request), MessageCodeConstant.M003_UPDATED, MessageConstant.SUBMITTED_SUCCESSFULLY);
    }

    @GetMapping("/attempts")
    @Operation(summary = "Get all exam attempts",
            description = "Get list of all exam attempts with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ExamMessageConstants.API_EXAM_ATTEMPTS_RETRIEVED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ExamMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ExamMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ExamResultResponse>>> getAllAttempts(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        return success(examAttemptService.getAllAttempts(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/my-attempts")
    @Operation(summary = "Get my exam attempts",
            description = "Get list of current user's exam attempts with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ExamMessageConstants.API_EXAM_MY_ATTEMPTS_RETRIEVED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ExamMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ExamMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ExamResultResponse>>> getMyAttempts(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {

        String userId = AuthUtils.getCurrentUserId();
        String profileId = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("ExamController_getMyAttempts_Profile not found for user: {}", userId);
                    return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                })
                .getId();

        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        return success(examAttemptService.getAttemptHistory(profileId, request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/{examId}/attempts")
    @Operation(summary = "Get exam attempts by exam ID",
            description = "Get list of exam attempts for a specific exam with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ExamMessageConstants.API_EXAM_ATTEMPTS_RETRIEVED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ExamMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ExamMessageConstants.EXAM_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ExamMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ExamResultResponse>>> getAttemptsByExamId(
            @PathVariable String examId,
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        return success(examAttemptService.getAttemptHistoryByExam(examId, request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }
}


