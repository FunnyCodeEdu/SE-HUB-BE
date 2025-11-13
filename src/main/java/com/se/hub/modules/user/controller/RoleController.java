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
import com.se.hub.modules.user.constant.role.RoleControllerConstants;
import com.se.hub.modules.user.dto.request.RoleCreationRequest;
import com.se.hub.modules.user.dto.request.RoleUpdateRequest;
import com.se.hub.modules.user.dto.response.RoleResponse;
import com.se.hub.modules.user.service.api.RoleService;
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

@Tag(name = "Role Management",
        description = "Role management API")
@RequestMapping("/roles")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    @Operation(summary = "Create new role",
            description = "Create a new role in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RoleControllerConstants.CREATE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "400", description = RoleControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "500", description = RoleControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<RoleResponse>> createRole(@Valid @RequestBody RoleCreationRequest request) {
        GenericResponse<RoleResponse> response = GenericResponse.<RoleResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M002_CREATED)
                        .messageDetail(MessageConstant.CREATED)
                        .build())
                .data(roleService.create(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all roles",
            description = "Get list of all roles with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RoleControllerConstants.GET_ALL_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "400", description = RoleControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "500", description = RoleControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<PagingResponse<RoleResponse>>> getRoles(
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

        GenericResponse<PagingResponse<RoleResponse>> response = GenericResponse.<PagingResponse<RoleResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(roleService.getAll(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roleName}")
    @Operation(summary = "Get role by name",
            description = "Get role information by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RoleControllerConstants.GET_BY_NAME_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "404", description = RoleControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = "500", description = RoleControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<RoleResponse>> getRoleByName(@PathVariable String roleName) {
        GenericResponse<RoleResponse> response = GenericResponse.<RoleResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(roleService.getByName(roleName))
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update role",
            description = "Update role information by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RoleControllerConstants.UPDATE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "400", description = RoleControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "404", description = RoleControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = "500", description = RoleControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<RoleResponse>> updateRole(
            @PathVariable String id,
            @Valid @RequestBody RoleUpdateRequest request) {
        GenericResponse<RoleResponse> response = GenericResponse.<RoleResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M003_UPDATED)
                        .messageDetail(MessageConstant.UPDATED)
                        .build())
                .data(roleService.updateById(id, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{name}")
    @Operation(summary = "Delete role",
            description = "Delete a role by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RoleControllerConstants.DELETE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "404", description = RoleControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = "500", description = RoleControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<Void>> deleteRole(@PathVariable String name) {
        roleService.deleteByName(name);
        
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
    @Operation(summary = "Delete all roles",
            description = "Delete all roles from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RoleControllerConstants.DELETE_ALL_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "500", description = RoleControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<Void>> deleteAllRoles() {
        roleService.deleteAll();
        
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
