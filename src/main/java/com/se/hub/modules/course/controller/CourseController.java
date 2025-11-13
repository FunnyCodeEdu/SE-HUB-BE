package com.se.hub.modules.course.controller;

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
import com.se.hub.modules.course.constant.CourseControllerConstants;
import com.se.hub.modules.course.dto.request.CreateCourseRequest;
import com.se.hub.modules.course.dto.request.UpdateCourseRequest;
import com.se.hub.modules.course.dto.response.CourseResponse;
import com.se.hub.modules.course.service.api.CourseService;
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

@Tag(name = "Course Management",
        description = "Course management API")
@RequestMapping("/api/v1/courses")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseController {
    CourseService courseService;

    @PostMapping
    @Operation(summary = "Create new course",
            description = "Create a new course in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = CourseControllerConstants.CREATE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "400", description = CourseControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "500", description = CourseControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<CourseResponse>> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        GenericResponse<CourseResponse> response = GenericResponse.<CourseResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M002_CREATED)
                        .messageDetail(MessageConstant.CREATED)
                        .build())
                .data(courseService.createCourse(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all courses",
            description = "Get list of all courses with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = CourseControllerConstants.GET_ALL_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "400", description = CourseControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "500", description = CourseControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<PagingResponse<CourseResponse>>> getCourses(
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

        GenericResponse<PagingResponse<CourseResponse>> response = GenericResponse.<PagingResponse<CourseResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(courseService.getCourses(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "Get course by ID",
            description = "Get course information by course ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = CourseControllerConstants.GET_BY_ID_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "404", description = CourseControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = "500", description = CourseControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<CourseResponse>> getCourseById(@PathVariable String courseId) {
        GenericResponse<CourseResponse> response = GenericResponse.<CourseResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(courseService.getById(courseId))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get courses by user ID",
            description = "Get list of courses for a specific user with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = CourseControllerConstants.GET_BY_USER_ID_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "400", description = CourseControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "500", description = CourseControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<PagingResponse<CourseResponse>>> getCoursesByUserId(
            @PathVariable String userId,
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

        GenericResponse<PagingResponse<CourseResponse>> response = GenericResponse.<PagingResponse<CourseResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(courseService.getCoursesByUserId(userId, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{courseId}")
    @Operation(summary = "Update course",
            description = "Update course information by course ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = CourseControllerConstants.UPDATE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "400", description = CourseControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "404", description = CourseControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = "500", description = CourseControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<CourseResponse>> updateCourse(
            @PathVariable String courseId,
            @Valid @RequestBody UpdateCourseRequest request) {
        GenericResponse<CourseResponse> response = GenericResponse.<CourseResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M003_UPDATED)
                        .messageDetail(MessageConstant.UPDATED)
                        .build())
                .data(courseService.updateCoursesById(courseId, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{courseId}")
    @Operation(summary = "Delete course",
            description = "Delete a course from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = CourseControllerConstants.DELETE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "404", description = CourseControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = "500", description = CourseControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<Void>> deleteCourse(@PathVariable String courseId) {
        courseService.deleteCourseById(courseId);
        
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
