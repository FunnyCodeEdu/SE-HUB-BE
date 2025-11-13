package com.se.hub.modules.lesson.controller;

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
import com.se.hub.modules.lesson.dto.request.CreateVocabularyRequest;
import com.se.hub.modules.lesson.dto.request.UpdateVocabularyRequest;
import com.se.hub.modules.lesson.dto.response.VocabularyResponse;
import com.se.hub.modules.lesson.service.api.VocabularyService;
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

@Tag(name = "Vocabulary Management",
        description = "Vocabulary management API")
@RequestMapping("/vocabularies")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VocabularyController {
    VocabularyService vocabularyService;

    @PostMapping
    @Operation(summary = "Create new vocabulary",
            description = "Create a new vocabulary in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vocabulary created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<VocabularyResponse>> createVocabulary(@Valid @RequestBody CreateVocabularyRequest request) {
        GenericResponse<VocabularyResponse> response = GenericResponse.<VocabularyResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M002_CREATED)
                        .messageDetail(MessageConstant.CREATED)
                        .build())
                .data(vocabularyService.createVocabulary(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all vocabularies",
            description = "Get list of all vocabularies with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vocabularies retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<VocabularyResponse>>> getVocabularies(
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

        GenericResponse<PagingResponse<VocabularyResponse>> response = GenericResponse.<PagingResponse<VocabularyResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(vocabularyService.getVocabularies(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{vocabularyId}")
    @Operation(summary = "Get vocabulary by ID",
            description = "Get vocabulary information by vocabulary ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vocabulary retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Vocabulary not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<VocabularyResponse>> getVocabularyById(@PathVariable String vocabularyId) {
        GenericResponse<VocabularyResponse> response = GenericResponse.<VocabularyResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(vocabularyService.getById(vocabularyId))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/level/{level}")
    @Operation(summary = "Get vocabularies by JLPT level",
            description = "Get list of vocabularies for a specific JLPT level with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vocabularies retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<VocabularyResponse>>> getVocabulariesByLevel(
            @PathVariable String level,
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

        GenericResponse<PagingResponse<VocabularyResponse>> response = GenericResponse.<PagingResponse<VocabularyResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(vocabularyService.getVocabulariesByLevel(level, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get vocabularies by type",
            description = "Get list of vocabularies for a specific type (HIRAGANA, KATAKANA, KANJI, OTHER) with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vocabularies retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<VocabularyResponse>>> getVocabulariesByType(
            @PathVariable String type,
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

        GenericResponse<PagingResponse<VocabularyResponse>> response = GenericResponse.<PagingResponse<VocabularyResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(vocabularyService.getVocabulariesByType(type, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{vocabularyId}")
    @Operation(summary = "Update vocabulary",
            description = "Update vocabulary information by vocabulary ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vocabulary updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Vocabulary not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<VocabularyResponse>> updateVocabulary(
            @PathVariable String vocabularyId,
            @Valid @RequestBody UpdateVocabularyRequest request) {
        GenericResponse<VocabularyResponse> response = GenericResponse.<VocabularyResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M003_UPDATED)
                        .messageDetail(MessageConstant.UPDATED)
                        .build())
                .data(vocabularyService.updateVocabularyById(vocabularyId, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{vocabularyId}")
    @Operation(summary = "Delete vocabulary",
            description = "Delete a vocabulary from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vocabulary deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Vocabulary not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<Void>> deleteVocabulary(@PathVariable String vocabularyId) {
        vocabularyService.deleteVocabularyById(vocabularyId);
        
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
