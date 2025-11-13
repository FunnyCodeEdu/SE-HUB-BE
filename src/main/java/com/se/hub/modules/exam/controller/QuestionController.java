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
import com.se.hub.modules.exam.dto.request.CreateQuestionRequest;
import com.se.hub.modules.exam.dto.request.UpdateQuestionRequest;
import com.se.hub.modules.exam.dto.response.QuestionResponse;
import com.se.hub.modules.exam.enums.QuestionCategory;
import com.se.hub.modules.exam.enums.QuestionDifficulty;
import com.se.hub.modules.exam.enums.QuestionType;
import com.se.hub.modules.exam.service.api.QuestionService;
import com.se.hub.modules.lesson.enums.JLPTLevel;
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

import java.util.List;

@Tag(name = "Question Management",
        description = "Question management API")
@RequestMapping("/api/v1/questions")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionController {
    QuestionService questionService;

    @PostMapping
    @Operation(summary = "Create new question",
            description = "Create a new question in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<QuestionResponse>> createQuestion(@Valid @RequestBody CreateQuestionRequest request) {
        GenericResponse<QuestionResponse> response = GenericResponse.<QuestionResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M002_CREATED)
                        .messageDetail(MessageConstant.CREATED)
                        .build())
                .data(questionService.createQuestion(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all questions",
            description = "Get list of all questions with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<QuestionResponse>>> getQuestions(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
            ) {

        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        GenericResponse<PagingResponse<QuestionResponse>> response = GenericResponse.<PagingResponse<QuestionResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(questionService.getAllQuestions(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{questionId}")
    @Operation(summary = "Get question by ID",
            description = "Get question information by question ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Question not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<QuestionResponse>> getQuestionById(@PathVariable String questionId) {
        GenericResponse<QuestionResponse> response = GenericResponse.<QuestionResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(questionService.getById(questionId))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get questions by category",
            description = "Get questions filtered by category with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<QuestionResponse>>> getQuestionsByCategory(
            @PathVariable QuestionCategory category,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
            ) {

        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        GenericResponse<PagingResponse<QuestionResponse>> response = GenericResponse.<PagingResponse<QuestionResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(questionService.getQuestionsByCategory(category, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/difficulty/{difficulty}")
    @Operation(summary = "Get questions by difficulty",
            description = "Get questions filtered by difficulty with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<QuestionResponse>>> getQuestionsByDifficulty(
            @PathVariable QuestionDifficulty difficulty,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
            ) {

        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        GenericResponse<PagingResponse<QuestionResponse>> response = GenericResponse.<PagingResponse<QuestionResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(questionService.getQuestionsByDifficulty(difficulty, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/jlpt/{jlptLevel}")
    @Operation(summary = "Get questions by JLPT level",
            description = "Get questions filtered by JLPT level with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<QuestionResponse>>> getQuestionsByJlptLevel(
            @PathVariable JLPTLevel jlptLevel,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
            ) {

        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        GenericResponse<PagingResponse<QuestionResponse>> response = GenericResponse.<PagingResponse<QuestionResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(questionService.getQuestionsByJlptLevel(jlptLevel, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{questionType}")
    @Operation(summary = "Get questions by type",
            description = "Get questions filtered by question type with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<QuestionResponse>>> getQuestionsByType(
            @PathVariable QuestionType questionType,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
            ) {

        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        GenericResponse<PagingResponse<QuestionResponse>> response = GenericResponse.<PagingResponse<QuestionResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(questionService.getQuestionsByType(questionType, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search questions by content",
            description = "Search questions by content keyword with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<QuestionResponse>>> searchQuestionsByContent(
            @RequestParam String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
            ) {

        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        GenericResponse<PagingResponse<QuestionResponse>> response = GenericResponse.<PagingResponse<QuestionResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(questionService.searchQuestionsByContent(keyword, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/random")
    @Operation(summary = "Get random questions",
            description = "Get random questions with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<List<QuestionResponse>>> getRandomQuestions(
            @RequestParam(required = false) QuestionCategory category,
            @RequestParam(required = false) QuestionDifficulty difficulty,
            @RequestParam(required = false) JLPTLevel jlptLevel,
            @RequestParam(required = false) QuestionType questionType,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit
            ) {

        GenericResponse<List<QuestionResponse>> response = GenericResponse.<List<QuestionResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(questionService.getRandomQuestions(category, difficulty, jlptLevel, questionType, limit))
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{questionId}")
    @Operation(summary = "Update question",
            description = "Update question information by question ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Question not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<QuestionResponse>> updateQuestion(
            @PathVariable String questionId,
            @Valid @RequestBody UpdateQuestionRequest request) {
        GenericResponse<QuestionResponse> response = GenericResponse.<QuestionResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M003_UPDATED)
                        .messageDetail(MessageConstant.UPDATED)
                        .build())
                .data(questionService.updateQuestion(questionId, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{questionId}")
    @Operation(summary = "Delete question",
            description = "Delete a question from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Question not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<Void>> deleteQuestion(@PathVariable String questionId) {
        questionService.deleteQuestion(questionId);
        
        GenericResponse<Void> response = GenericResponse.<Void>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M004_DELETED)
                        .messageDetail(MessageConstant.DELETED)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }
}
