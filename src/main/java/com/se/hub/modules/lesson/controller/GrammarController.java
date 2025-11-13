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
import com.se.hub.modules.lesson.dto.request.CreateGrammarRequest;
import com.se.hub.modules.lesson.dto.request.UpdateGrammarRequest;
import com.se.hub.modules.lesson.dto.response.GrammarResponse;
import com.se.hub.modules.lesson.service.api.GrammarService;
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

@Tag(name = "Grammar Management",
        description = "Grammar management API")
@RequestMapping("/grammars")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GrammarController {
    GrammarService grammarService;

    @PostMapping
    @Operation(summary = "Create new grammar",
            description = "Create a new grammar in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grammar created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<GrammarResponse>> createGrammar(@Valid @RequestBody CreateGrammarRequest request) {
        GenericResponse<GrammarResponse> response = GenericResponse.<GrammarResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M002_CREATED)
                        .messageDetail(MessageConstant.CREATED)
                        .build())
                .data(grammarService.createGrammar(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all grammars",
            description = "Get list of all grammars with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grammars retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<GrammarResponse>>> getGrammars(
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

        GenericResponse<PagingResponse<GrammarResponse>> response = GenericResponse.<PagingResponse<GrammarResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(grammarService.getGrammars(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{grammarId}")
    @Operation(summary = "Get grammar by ID",
            description = "Get grammar information by grammar ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grammar retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Grammar not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<GrammarResponse>> getGrammarById(@PathVariable String grammarId) {
        GenericResponse<GrammarResponse> response = GenericResponse.<GrammarResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(grammarService.getById(grammarId))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/level/{level}")
    @Operation(summary = "Get grammars by JLPT level",
            description = "Get list of grammars for a specific JLPT level with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grammars retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<GrammarResponse>>> getGrammarsByLevel(
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

        GenericResponse<PagingResponse<GrammarResponse>> response = GenericResponse.<PagingResponse<GrammarResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(grammarService.getGrammarsByLevel(level, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{grammarId}")
    @Operation(summary = "Update grammar",
            description = "Update grammar information by grammar ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grammar updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Grammar not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<GrammarResponse>> updateGrammar(
            @PathVariable String grammarId,
            @Valid @RequestBody UpdateGrammarRequest request) {
        GenericResponse<GrammarResponse> response = GenericResponse.<GrammarResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M003_UPDATED)
                        .messageDetail(MessageConstant.UPDATED)
                        .build())
                .data(grammarService.updateGrammarById(grammarId, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{grammarId}")
    @Operation(summary = "Delete grammar",
            description = "Delete a grammar from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grammar deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Grammar not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<Void>> deleteGrammar(@PathVariable String grammarId) {
        grammarService.deleteGrammarById(grammarId);
        
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
