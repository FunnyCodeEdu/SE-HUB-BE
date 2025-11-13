package com.catsocute.japanlearn_hub.modules.user.controller;

import com.catsocute.japanlearn_hub.common.constant.ApiConstant;
import com.catsocute.japanlearn_hub.common.constant.BaseFieldConstant;
import com.catsocute.japanlearn_hub.common.constant.MessageCodeConstant;
import com.catsocute.japanlearn_hub.common.constant.MessageConstant;
import com.catsocute.japanlearn_hub.common.constant.PaginationConstants;
import com.catsocute.japanlearn_hub.common.dto.MessageDTO;
import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.request.SortRequest;
import com.catsocute.japanlearn_hub.common.dto.response.GenericResponse;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.modules.user.constant.user.UserControllerConstants;
import com.catsocute.japanlearn_hub.modules.user.dto.request.ChangePasswordRequest;
import com.catsocute.japanlearn_hub.modules.user.dto.request.UserCreationRequest;
import com.catsocute.japanlearn_hub.modules.user.dto.request.UserRolesUpdateRequest;
import com.catsocute.japanlearn_hub.modules.user.dto.response.UserResponse;
import com.catsocute.japanlearn_hub.modules.user.service.api.UserService;
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


@Tag(name = "User Management",
        description = "User management API")
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    @Operation(summary = "Create new user",
            description = "Create a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = UserControllerConstants.CREATE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "400", description = UserControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "500", description = UserControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<UserResponse>> createUser(@Valid @RequestBody UserCreationRequest request) {
        GenericResponse<UserResponse> response = GenericResponse.<UserResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M002_CREATED)
                        .messageDetail(MessageConstant.CREATED)
                        .build())
                .data(userService.create(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all users",
            description = "Get list of all users with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = UserControllerConstants.GET_ALL_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "400", description = UserControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "500", description = UserControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<PagingResponse<UserResponse>>> getUsers(
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

        GenericResponse<PagingResponse<UserResponse>> response = GenericResponse.<PagingResponse<UserResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(userService.getUsers(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get my information",
            description = "Get current user's personal information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = UserControllerConstants.GET_MY_INFO_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "401", description = UserControllerConstants.UNAUTHORIZED_RESPONSE),
            @ApiResponse(responseCode = "500", description = UserControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<UserResponse>> getMyInfo() {
        GenericResponse<UserResponse> response = GenericResponse.<UserResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(userService.getMyInfo())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password",
            description = "Change password for current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = UserControllerConstants.CHANGE_PASSWORD_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "400", description = UserControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "401", description = UserControllerConstants.UNAUTHORIZED_RESPONSE),
            @ApiResponse(responseCode = "500", description = UserControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<UserResponse>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        GenericResponse<UserResponse> response = GenericResponse.<UserResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M003_UPDATED)
                        .messageDetail(MessageConstant.UPDATED)
                        .build())
                .data(userService.changePassword(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user roles",
            description = "Update roles list for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = UserControllerConstants.UPDATE_ROLES_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "400", description = UserControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "401", description = UserControllerConstants.UNAUTHORIZED_RESPONSE),
            @ApiResponse(responseCode = "403", description = UserControllerConstants.FORBIDDEN_RESPONSE),
            @ApiResponse(responseCode = "404", description = UserControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = "500", description = UserControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<UserResponse>> updateUserRoles(
            @PathVariable String userId,
            @Valid @RequestBody UserRolesUpdateRequest request) {
        GenericResponse<UserResponse> response = GenericResponse.<UserResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M003_UPDATED)
                        .messageDetail(MessageConstant.UPDATED)
                        .build())
                .data(userService.updateRoles(userId, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user",
            description = "Delete a user from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = UserControllerConstants.DELETE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "401", description = UserControllerConstants.UNAUTHORIZED_RESPONSE),
            @ApiResponse(responseCode = "403", description = UserControllerConstants.FORBIDDEN_RESPONSE),
            @ApiResponse(responseCode = "404", description = UserControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = "500", description = UserControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<Void>> deleteUser(@PathVariable String userId) {
        userService.deleteById(userId);
        
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
