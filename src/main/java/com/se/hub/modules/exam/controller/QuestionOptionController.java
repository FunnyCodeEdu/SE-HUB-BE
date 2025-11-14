package com.se.hub.modules.exam.controller;

import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.constant.ResponseCode;
import com.se.hub.common.controller.BaseController;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.modules.exam.constant.QuestionOptionMessageConstants;
import com.se.hub.modules.exam.dto.request.UpdateQuestionOptionRequest;
import com.se.hub.modules.exam.dto.response.QuestionOptionResponse;
import com.se.hub.modules.exam.service.QuestionOptionService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "Question Option Management",
        description = "Question option management API")
@RequestMapping("/question-options")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class QuestionOptionController extends BaseController {
    QuestionOptionService questionOptionService;

    @GetMapping("/{optionId}")
    @Operation(summary = "Get question option by ID",
            description = "Get question option information by option ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = QuestionOptionMessageConstants.API_QUESTION_OPTION_RETRIEVED_BY_ID_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = QuestionOptionMessageConstants.QUESTION_OPTION_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = QuestionOptionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<QuestionOptionResponse>> getQuestionOptionById(@PathVariable String optionId) {
        return success(questionOptionService.getById(optionId), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/question/{questionId}")
    @Operation(summary = "Get options by question ID",
            description = "Get all options for a specific question")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = QuestionOptionMessageConstants.API_QUESTION_OPTION_RETRIEVED_BY_QUESTION_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = QuestionOptionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = QuestionOptionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<List<QuestionOptionResponse>>> getOptionsByQuestionId(@PathVariable String questionId) {
        return success(questionOptionService.getOptionsByQuestionId(questionId), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/question/{questionId}/correct")
    @Operation(summary = "Get correct options by question ID",
            description = "Get all correct options for a specific question")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = QuestionOptionMessageConstants.API_QUESTION_OPTION_RETRIEVED_CORRECT_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = QuestionOptionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = QuestionOptionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<List<QuestionOptionResponse>>> getCorrectOptionsByQuestionId(@PathVariable String questionId) {
        return success(questionOptionService.getCorrectOptionsByQuestionId(questionId), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @PutMapping("/{optionId}")
    @Operation(summary = "Update question option",
            description = "Update question option information by option ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = QuestionOptionMessageConstants.API_QUESTION_OPTION_UPDATED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = QuestionOptionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = QuestionOptionMessageConstants.QUESTION_OPTION_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = QuestionOptionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<QuestionOptionResponse>> updateQuestionOption(
            @PathVariable String optionId,
            @Valid @RequestBody UpdateQuestionOptionRequest request) {
        return success(questionOptionService.updateQuestionOption(optionId, request), MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @DeleteMapping("/{optionId}")
    @Operation(summary = "Delete question option",
            description = "Delete a question option from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = QuestionOptionMessageConstants.API_QUESTION_OPTION_DELETED_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = QuestionOptionMessageConstants.QUESTION_OPTION_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = QuestionOptionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<Void>> deleteQuestionOption(@PathVariable String optionId) {
        questionOptionService.deleteQuestionOption(optionId);
        return success(null, MessageCodeConstant.M004_DELETED, MessageConstant.DELETED);
    }

    @DeleteMapping("/question/{questionId}")
    @Operation(summary = "Delete all options for a question",
            description = "Delete all question options for a specific question")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = QuestionOptionMessageConstants.API_QUESTION_OPTIONS_DELETED_SUCCESS, 
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = QuestionOptionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = QuestionOptionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<Void>> deleteOptionsByQuestionId(@PathVariable String questionId) {
        questionOptionService.deleteOptionsByQuestionId(questionId);
        return success(null, MessageCodeConstant.M004_DELETED, MessageConstant.DELETED);
    }
}
