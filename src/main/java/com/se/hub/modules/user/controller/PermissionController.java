package com.se.hub.modules.user.controller;

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
import com.se.hub.modules.user.constant.permission.PermissionControllerConstants;
import com.se.hub.modules.user.dto.request.PermissionCreationRequest;
import com.se.hub.modules.user.dto.response.PermissionResponse;
import com.se.hub.modules.user.service.api.PermissionService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Permission Management",
        description = "Permission management API")
@RequestMapping("/api/v1/permissions")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    @Operation(summary = "Create new permission",
            description = "Create a new permission in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = PermissionControllerConstants.CREATE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "400", description = PermissionControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "500", description = PermissionControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<PermissionResponse>> createPermission(@Valid @RequestBody PermissionCreationRequest request) {
        GenericResponse<PermissionResponse> response = GenericResponse.<PermissionResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M002_CREATED)
                        .messageDetail(MessageConstant.CREATED)
                        .build())
                .data(permissionService.create(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all permissions",
            description = "Get list of all permissions with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = PermissionControllerConstants.GET_ALL_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "400", description = PermissionControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "500", description = PermissionControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<PagingResponse<PermissionResponse>>> getPermissions(
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

        GenericResponse<PagingResponse<PermissionResponse>> response = GenericResponse.<PagingResponse<PermissionResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(permissionService.getAll(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{name}")
    @Operation(summary = "Get permission by name",
            description = "Get permission information by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = PermissionControllerConstants.GET_BY_NAME_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "404", description = PermissionControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = "500", description = PermissionControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<PermissionResponse>> getPermissionByName(@PathVariable String name) {
        GenericResponse<PermissionResponse> response = GenericResponse.<PermissionResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(permissionService.getByName(name))
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete permission",
            description = "Delete a permission by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = PermissionControllerConstants.DELETE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "404", description = PermissionControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = "500", description = PermissionControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<Void>> deletePermission(@PathVariable String id) {
        permissionService.deleteById(id);
        
        GenericResponse<Void> response = GenericResponse.<Void>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M004_DELETED)
                        .messageDetail(MessageConstant.DELETED)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(summary = "Delete all permissions",
            description = "Delete all permissions from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = PermissionControllerConstants.DELETE_ALL_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "500", description = PermissionControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<Void>> deleteAllPermissions() {
        permissionService.deleteAll();
        
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
