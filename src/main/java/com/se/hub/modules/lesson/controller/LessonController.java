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
import com.se.hub.modules.lesson.dto.request.AddContentToLessonRequest;
import com.se.hub.modules.lesson.dto.request.UpdateLessonRequest;
import com.se.hub.modules.lesson.dto.response.LessonResponse;
import com.se.hub.modules.lesson.enums.LessonType;
import com.se.hub.modules.lesson.service.api.ContentServiceFactory;
import com.se.hub.modules.lesson.service.api.LessonService;
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

@Tag(name = "Lesson Management",
        description = "Lesson management API")
@RequestMapping("/lessons")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonController {
    LessonService lessonService;
    ContentServiceFactory  contentServiceFactory;

    @PostMapping("/{lessonId}/content")
    @Operation(summary = "Add content to lesson",
            description = "Add content to lesson")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Add content successfully"),
            @ApiResponse(responseCode = "404", description = "Lesson not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<String>> addContentToLesson(
            @PathVariable String lessonId,
            @RequestBody AddContentToLessonRequest request) {

        LessonType lessonType = LessonType.valueOf(request.getLessonType());
        contentServiceFactory.getService(lessonType).addContent(lessonId, request);

        GenericResponse<String> response = GenericResponse.<String>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data("Successfully")
                .build();

        return ResponseEntity.ok(response);
    }


    @GetMapping
    @Operation(summary = "Get all lessons",
            description = "Get list of all lessons with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lessons retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<LessonResponse>>> getLessons(
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

        GenericResponse<PagingResponse<LessonResponse>> response = GenericResponse.<PagingResponse<LessonResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(lessonService.getLessons(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{lessonId}")
    @Operation(summary = "Get lesson by ID",
            description = "Get lesson information by lesson ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lesson retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Lesson not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<LessonResponse>> getLessonById(@PathVariable String lessonId) {
        GenericResponse<LessonResponse> response = GenericResponse.<LessonResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(lessonService.getById(lessonId))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get lessons by type",
            description = "Get list of lessons for a specific type (GRAMMAR, VOCABULARY) with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lessons retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<LessonResponse>>> getLessonsByType(
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

        GenericResponse<PagingResponse<LessonResponse>> response = GenericResponse.<PagingResponse<LessonResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(lessonService.getLessonsByType(type, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/parent/{parentLessonId}")
    @Operation(summary = "Get lessons by parent lesson",
            description = "Get list of child lessons for a specific parent lesson with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lessons retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<LessonResponse>>> getLessonsByParent(
            @PathVariable String parentLessonId,
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

        GenericResponse<PagingResponse<LessonResponse>> response = GenericResponse.<PagingResponse<LessonResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(lessonService.getLessonsByParent(parentLessonId, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{lessonId}")
    @Operation(summary = "Update lesson",
            description = "Update lesson information by lesson ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lesson updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Lesson not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<LessonResponse>> updateLesson(
            @PathVariable String lessonId,
            @Valid @RequestBody UpdateLessonRequest request) {
        GenericResponse<LessonResponse> response = GenericResponse.<LessonResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M003_UPDATED)
                        .messageDetail(MessageConstant.UPDATED)
                        .build())
                .data(lessonService.updateLessonById(lessonId, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{lessonId}")
    @Operation(summary = "Delete lesson",
            description = "Delete a lesson from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lesson deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Lesson not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<Void>> deleteLesson(@PathVariable String lessonId) {
        lessonService.deleteLessonById(lessonId);
        
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
