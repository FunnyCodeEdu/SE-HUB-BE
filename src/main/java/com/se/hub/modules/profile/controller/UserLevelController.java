package com.se.hub.modules.profile.controller;

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
import com.se.hub.modules.profile.constant.userlevel.UserLevelControllerConstants;
import com.se.hub.modules.profile.dto.response.UserLevelResponse;
import com.se.hub.modules.profile.service.api.UserLevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Level Management",
        description = "User level management API")
@RequestMapping("/levels")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserLevelController extends BaseController {
    UserLevelService userLevelService;

    @GetMapping
    @Operation(summary = "Get all user levels",
            description = "Get list of all user levels with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = UserLevelControllerConstants.GET_ALL_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = UserLevelControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = UserLevelControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<PagingResponse<UserLevelResponse>>> getAllUserLevels(
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

        return success(userLevelService.getAllUserLevels(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/{userLevelId}")
    @Operation(summary = "Get user level by ID",
            description = "Get user level details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = UserLevelControllerConstants.GET_BY_ID_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = UserLevelControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = UserLevelControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<UserLevelResponse>> getUserLevelById(@PathVariable String userLevelId) {
        return success(userLevelService.getUserLevelById(userLevelId), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @DeleteMapping("/{userLevelId}")
    @Operation(summary = "Delete user level by ID",
            description = "Delete user level by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = UserLevelControllerConstants.DELETE_BY_ID_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "401", description = UserLevelControllerConstants.UNAUTHORIZED_RESPONSE),
            @ApiResponse(responseCode = "403", description = UserLevelControllerConstants.FORBIDDEN_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = UserLevelControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = UserLevelControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<Void>> deleteUserLevelById(@PathVariable String userLevelId) {
        userLevelService.deleteById(userLevelId);
        return success(null, MessageCodeConstant.M004_DELETED, MessageConstant.DELETED);
    }
}
