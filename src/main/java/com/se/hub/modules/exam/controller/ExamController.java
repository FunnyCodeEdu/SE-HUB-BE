package com.se.hub.modules.exam.controller;

import com.se.hub.common.constant.ApiConstant;
import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.constant.PaginationConstants;
import com.se.hub.common.dto.MessageDTO;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.request.SortRequest;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.exam.dto.request.AddQuestionsToExamRequest;
import com.se.hub.modules.exam.dto.request.CreateExamRequest;
import com.se.hub.modules.exam.dto.request.RemoveQuestionsFromExamRequest;
import com.se.hub.modules.exam.dto.request.SubmitExamRequest;
import com.se.hub.modules.exam.dto.request.UpdateExamRequest;
import com.se.hub.modules.exam.dto.response.ExamResponse;
import com.se.hub.modules.exam.dto.response.ExamResultResponse;
import com.se.hub.modules.exam.service.api.ExamAttemptService;
import com.se.hub.modules.exam.service.api.ExamService;
import com.se.hub.modules.profile.repository.ProfileRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Exam Management",
        description = "Exam management API")
@RequestMapping("/exams")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamController {
    ExamService examService;
    ExamAttemptService examAttemptService;
    ProfileRepository profileRepository;

    @PostMapping
    @Operation(summary = "Create new exam",
            description = "Create a new exam")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<GenericResponse<ExamResponse>> create(@Valid @RequestBody CreateExamRequest request) {
        GenericResponse<ExamResponse> response = GenericResponse.<ExamResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M002_CREATED)
                        .messageDetail(MessageConstant.CREATED)
                        .build())
                .data(examService.create(request))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all exams",
            description = "Get list of exams with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<ExamResponse>>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        GenericResponse<PagingResponse<ExamResponse>> response = GenericResponse.<PagingResponse<ExamResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(examService.getAll(request))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get exams by course ID",
            description = "Get list of exams for a specific course with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<ExamResponse>>> getByCourseId(
            @PathVariable String courseId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        GenericResponse<PagingResponse<ExamResponse>> response = GenericResponse.<PagingResponse<ExamResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(examService.getByCourseId(courseId, request))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{examId}")
    @Operation(summary = "Get exam by ID",
            description = "Get exam by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<GenericResponse<ExamResponse>> getById(@PathVariable String examId) {
        GenericResponse<ExamResponse> response = GenericResponse.<ExamResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(examService.getById(examId))
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{examId}")
    @Operation(summary = "Update exam",
            description = "Update exam by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<GenericResponse<ExamResponse>> update(
            @PathVariable String examId,
            @Valid @RequestBody UpdateExamRequest request) {
        GenericResponse<ExamResponse> response = GenericResponse.<ExamResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M003_UPDATED)
                        .messageDetail(MessageConstant.UPDATED)
                        .build())
                .data(examService.updateById(examId, request))
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{examId}")
    @Operation(summary = "Delete exam",
            description = "Delete exam by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<GenericResponse<Void>> delete(@PathVariable String examId) {
        examService.deleteById(examId);
        GenericResponse<Void> response = GenericResponse.<Void>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M004_DELETED)
                        .messageDetail(MessageConstant.DELETED)
                        .build())
                .build();
        return ResponseEntity.ok(response);
    }

    // Dedicated question endpoints
    @PostMapping("/{examId}/questions:add")
    @Operation(summary = "Add questions to exam",
            description = "Add questions to exam by a list of questionIds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<GenericResponse<ExamResponse>> addQuestions(
            @PathVariable String examId,
            @Valid @RequestBody AddQuestionsToExamRequest request) {
        GenericResponse<ExamResponse> response = GenericResponse.<ExamResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M003_UPDATED)
                        .messageDetail(MessageConstant.UPDATED)
                        .build())
                .data(examService.addQuestions(examId, request))
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{examId}/questions:remove")
    @Operation(summary = "Remove questions from exam",
            description = "Remove questions from exam by a list of questionIds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<GenericResponse<ExamResponse>> removeQuestions(
            @PathVariable String examId,
            @Valid @RequestBody RemoveQuestionsFromExamRequest request) {
        GenericResponse<ExamResponse> response = GenericResponse.<ExamResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M003_UPDATED)
                        .messageDetail(MessageConstant.UPDATED)
                        .build())
                .data(examService.removeQuestions(examId, request))
                .build();
        return ResponseEntity.ok(response);
    }

    // Exam submission and scoring endpoints
    @PostMapping("/submit")
    @Operation(summary = "Submit exam and get results",
            description = "Submit exam answers and receive detailed scoring results")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Exam not found"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<GenericResponse<ExamResultResponse>> submitExam(
            @Valid @RequestBody SubmitExamRequest request) {
        GenericResponse<ExamResultResponse> response = GenericResponse.<ExamResultResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M003_UPDATED)
                        .messageDetail(MessageConstant.SUBMITTED_SUCCESSFULLY)
                        .build())
                .data(examAttemptService.submitExam(request))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/attempts")
    @Operation(summary = "Get all exam attempts",
            description = "Get list of all exam attempts with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<ExamResultResponse>>> getAllAttempts(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        GenericResponse<PagingResponse<ExamResultResponse>> response = GenericResponse.<PagingResponse<ExamResultResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(examAttemptService.getAllAttempts(request))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-attempts")
    @Operation(summary = "Get my exam attempts",
            description = "Get list of current user's exam attempts with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<ExamResultResponse>>> getMyAttempts(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {

        String userId = AuthUtils.getCurrentUserId();
        String profileId = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user"))
                .getId();

        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        GenericResponse<PagingResponse<ExamResultResponse>> response = GenericResponse.<PagingResponse<ExamResultResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(examAttemptService.getAttemptHistory(profileId, request))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{examId}/attempts")
    @Operation(summary = "Get exam attempts by exam ID",
            description = "Get list of exam attempts for a specific exam with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Exam not found"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<ExamResultResponse>>> getAttemptsByExamId(
            @PathVariable String examId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        GenericResponse<PagingResponse<ExamResultResponse>> response = GenericResponse.<PagingResponse<ExamResultResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(examAttemptService.getAttemptHistoryByExam(examId, request))
                .build();
        return ResponseEntity.ok(response);
    }
}


