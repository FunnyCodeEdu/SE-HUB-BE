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
import com.se.hub.modules.exam.constant.QuestionMessageConstants;
import com.se.hub.modules.exam.dto.request.CreateQuestionRequest;
import com.se.hub.modules.exam.dto.request.UpdateQuestionRequest;
import com.se.hub.modules.exam.dto.response.QuestionResponse;
import com.se.hub.modules.exam.enums.QuestionCategory;
import com.se.hub.modules.exam.enums.QuestionDifficulty;
import com.se.hub.modules.exam.enums.QuestionType;
import com.se.hub.modules.exam.service.QuestionService;
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

import java.util.List;

@Slf4j
@Tag(name = "Question Management",
        description = "Question management API")
@RequestMapping("/questions")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class QuestionController extends BaseController {
    QuestionService questionService;

    @PostMapping
    @Operation(summary = "Create new question",
            description = "Create a new question in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = QuestionMessageConstants.API_QUESTION_CREATED_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = QuestionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = QuestionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<QuestionResponse>> createQuestion(@Valid @RequestBody CreateQuestionRequest request) {
        QuestionResponse questionResponse = questionService.createQuestion(request);
        return success(questionResponse, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all questions",
            description = "Get list of all questions with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = QuestionMessageConstants.API_QUESTION_RETRIEVED_ALL_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = QuestionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = QuestionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<QuestionResponse>>> getQuestions(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
            ) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        return success(questionService.getAllQuestions(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/{questionId}")
    @Operation(summary = "Get question by ID",
            description = "Get question information by question ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = QuestionMessageConstants.API_QUESTION_RETRIEVED_BY_ID_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = QuestionMessageConstants.QUESTION_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = QuestionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<QuestionResponse>> getQuestionById(@PathVariable String questionId) {
        return success(questionService.getById(questionId), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get questions by category",
            description = "Get questions filtered by category with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = QuestionMessageConstants.API_QUESTION_RETRIEVED_BY_CATEGORY_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = QuestionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = QuestionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<QuestionResponse>>> getQuestionsByCategory(
            @PathVariable QuestionCategory category,
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
            ) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        return success(questionService.getQuestionsByCategory(category, request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/difficulty/{difficulty}")
    @Operation(summary = "Get questions by difficulty",
            description = "Get questions filtered by difficulty with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = QuestionMessageConstants.API_QUESTION_RETRIEVED_BY_DIFFICULTY_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = QuestionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = QuestionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<QuestionResponse>>> getQuestionsByDifficulty(
            @PathVariable QuestionDifficulty difficulty,
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
            ) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        return success(questionService.getQuestionsByDifficulty(difficulty, request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/type/{questionType}")
    @Operation(summary = "Get questions by type",
            description = "Get questions filtered by question type with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = QuestionMessageConstants.API_QUESTION_RETRIEVED_BY_TYPE_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = QuestionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = QuestionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<QuestionResponse>>> getQuestionsByType(
            @PathVariable QuestionType questionType,
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
            ) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        return success(questionService.getQuestionsByType(questionType, request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/search")
    @Operation(summary = "Search questions by content",
            description = "Search questions by content keyword with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = QuestionMessageConstants.API_QUESTION_SEARCH_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = QuestionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = QuestionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<QuestionResponse>>> searchQuestionsByContent(
            @RequestParam String keyword,
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
            ) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        return success(questionService.searchQuestionsByContent(keyword, request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/random")
    @Operation(summary = "Get random questions",
            description = "Get random questions with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = QuestionMessageConstants.API_QUESTION_RANDOM_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = QuestionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = QuestionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<List<QuestionResponse>>> getRandomQuestions(
            @RequestParam(required = false) QuestionCategory category,
            @RequestParam(required = false) QuestionDifficulty difficulty,
            @RequestParam(required = false) QuestionType questionType,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit
            ) {
        return success(questionService.getRandomQuestions(category, difficulty, questionType, limit), 
                MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @PutMapping("/{questionId}")
    @Operation(summary = "Update question",
            description = "Update question information by question ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = QuestionMessageConstants.API_QUESTION_UPDATED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = QuestionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = QuestionMessageConstants.QUESTION_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = QuestionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<QuestionResponse>> updateQuestion(
            @PathVariable String questionId,
            @Valid @RequestBody UpdateQuestionRequest request) {
        return success(questionService.updateQuestion(questionId, request), MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @DeleteMapping("/{questionId}")
    @Operation(summary = "Delete question",
            description = "Delete a question from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = QuestionMessageConstants.API_QUESTION_DELETED_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = QuestionMessageConstants.QUESTION_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = QuestionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<Void>> deleteQuestion(@PathVariable String questionId) {
        questionService.deleteQuestion(questionId);
        return success(null, MessageCodeConstant.M004_DELETED, MessageConstant.DELETED);
    }
}
