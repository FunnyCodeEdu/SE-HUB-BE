package com.catsocute.japanlearn_hub.modules.exam.controller;

import com.catsocute.japanlearn_hub.common.constant.ApiConstant;
import com.catsocute.japanlearn_hub.common.constant.MessageCodeConstant;
import com.catsocute.japanlearn_hub.common.constant.MessageConstant;
import com.catsocute.japanlearn_hub.common.dto.MessageDTO;
import com.catsocute.japanlearn_hub.common.dto.response.GenericResponse;
import com.catsocute.japanlearn_hub.modules.exam.dto.request.UpdateQuestionOptionRequest;
import com.catsocute.japanlearn_hub.modules.exam.dto.response.QuestionOptionResponse;
import com.catsocute.japanlearn_hub.modules.exam.service.api.QuestionOptionService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Question Option Management",
        description = "Question option management API")
@RequestMapping("/api/v1/question-options")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionOptionController {
    QuestionOptionService questionOptionService;

    @GetMapping("/{optionId}")
    @Operation(summary = "Get question option by ID",
            description = "Get question option information by option ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question option retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Question option not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<QuestionOptionResponse>> getQuestionOptionById(@PathVariable String optionId) {
        GenericResponse<QuestionOptionResponse> response = GenericResponse.<QuestionOptionResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(questionOptionService.getById(optionId))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/question/{questionId}")
    @Operation(summary = "Get options by question ID",
            description = "Get all options for a specific question")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question options retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<List<QuestionOptionResponse>>> getOptionsByQuestionId(@PathVariable String questionId) {
        GenericResponse<List<QuestionOptionResponse>> response = GenericResponse.<List<QuestionOptionResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(questionOptionService.getOptionsByQuestionId(questionId))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/question/{questionId}/correct")
    @Operation(summary = "Get correct options by question ID",
            description = "Get all correct options for a specific question")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Correct question options retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<List<QuestionOptionResponse>>> getCorrectOptionsByQuestionId(@PathVariable String questionId) {
        GenericResponse<List<QuestionOptionResponse>> response = GenericResponse.<List<QuestionOptionResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(questionOptionService.getCorrectOptionsByQuestionId(questionId))
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{optionId}")
    @Operation(summary = "Update question option",
            description = "Update question option information by option ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question option updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Question option not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<QuestionOptionResponse>> updateQuestionOption(
            @PathVariable String optionId,
            @Valid @RequestBody UpdateQuestionOptionRequest request) {
        GenericResponse<QuestionOptionResponse> response = GenericResponse.<QuestionOptionResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M003_UPDATED)
                        .messageDetail(MessageConstant.UPDATED)
                        .build())
                .data(questionOptionService.updateQuestionOption(optionId, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{optionId}")
    @Operation(summary = "Delete question option",
            description = "Delete a question option from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question option deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Question option not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<Void>> deleteQuestionOption(@PathVariable String optionId) {
        questionOptionService.deleteQuestionOption(optionId);
        
        GenericResponse<Void> response = GenericResponse.<Void>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M004_DELETED)
                        .messageDetail(MessageConstant.DELETED)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/question/{questionId}")
    @Operation(summary = "Delete all options for a question",
            description = "Delete all question options for a specific question")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question options deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<Void>> deleteOptionsByQuestionId(@PathVariable String questionId) {
        questionOptionService.deleteOptionsByQuestionId(questionId);
        
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
