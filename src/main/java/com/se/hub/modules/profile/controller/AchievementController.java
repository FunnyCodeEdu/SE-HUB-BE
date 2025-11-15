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
import com.se.hub.modules.profile.constant.achievement.AchievementControllerConstants;
import com.se.hub.modules.profile.dto.request.CreateAchievementRequest;
import com.se.hub.modules.profile.dto.request.UpdateAchievementRequest;
import com.se.hub.modules.profile.dto.response.AchievementResponse;
import com.se.hub.modules.profile.enums.AchievementEnums;
import com.se.hub.modules.profile.service.api.AchievementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Achievement Management",
        description = "Achievement management API")
@RequestMapping("/achievements")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class AchievementController extends BaseController {
    AchievementService achievementService;

    @PostMapping
    @Operation(summary = "Create new achievement",
            description = "Create a new achievement in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = AchievementControllerConstants.CREATE_SUCCESS_RESPONSE,
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = AchievementControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = AchievementControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<AchievementResponse>> createAchievement(@Valid @RequestBody CreateAchievementRequest request) {
        AchievementResponse achievementResponse = achievementService.createAchievement(request);
        return success(achievementResponse, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all achievements",
            description = "Get list of all achievements with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = AchievementControllerConstants.GET_ALL_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = AchievementControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = AchievementControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<PagingResponse<AchievementResponse>>> getAllAchievements(
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

        return success(achievementService.getAllAchievements(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/me")
    @Operation(summary = "Get my achievements",
            description = "Get current user's achievements")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = AchievementControllerConstants.GET_MY_ACHIEVEMENTS_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "401", description = AchievementControllerConstants.UNAUTHORIZED_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = AchievementControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<PagingResponse<AchievementResponse>>> getMyAchievements(
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

        return success(achievementService.getMyAchievements(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/{achievementId}")
    @Operation(summary = "Get achievement by ID",
            description = "Get achievement details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = AchievementControllerConstants.GET_BY_ID_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = AchievementControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = AchievementControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<AchievementResponse>> getAchievementById(@PathVariable String achievementId) {
        return success(achievementService.getAchievementById(achievementId), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @PutMapping("/{achievementId}")
    @Operation(summary = "Update achievement by ID",
            description = "Update achievement details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = AchievementControllerConstants.UPDATE_BY_ID_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = AchievementControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "401", description = AchievementControllerConstants.UNAUTHORIZED_RESPONSE),
            @ApiResponse(responseCode = "403", description = AchievementControllerConstants.FORBIDDEN_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = AchievementControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = AchievementControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<AchievementResponse>> updateAchievementById(
            @PathVariable String achievementId,
            @Valid @RequestBody UpdateAchievementRequest request) {
        return success(achievementService.updateById(achievementId, request), MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @PutMapping("/type/{achievementType}")
    @Operation(summary = "Update achievement by type",
            description = "Update achievement details by achievement type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = AchievementControllerConstants.UPDATE_BY_TYPE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = AchievementControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "401", description = AchievementControllerConstants.UNAUTHORIZED_RESPONSE),
            @ApiResponse(responseCode = "403", description = AchievementControllerConstants.FORBIDDEN_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = AchievementControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = AchievementControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<AchievementResponse>> updateAchievementByType(
            @PathVariable AchievementEnums achievementType,
            @Valid @RequestBody UpdateAchievementRequest request) {
        return success(achievementService.updateByAchievementType(achievementType, request), MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @DeleteMapping("/{achievementId}")
    @Operation(summary = "Delete achievement by ID",
            description = "Delete achievement by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = AchievementControllerConstants.DELETE_BY_ID_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "401", description = AchievementControllerConstants.UNAUTHORIZED_RESPONSE),
            @ApiResponse(responseCode = "403", description = AchievementControllerConstants.FORBIDDEN_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = AchievementControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = AchievementControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<Void>> deleteAchievementById(@PathVariable String achievementId) {
        achievementService.deleteById(achievementId);
        return success(null, MessageCodeConstant.M004_DELETED, MessageConstant.DELETED);
    }

    @DeleteMapping("/type/{achievementType}")
    @Operation(summary = "Delete achievement by type",
            description = "Delete achievement by achievement type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = AchievementControllerConstants.DELETE_BY_TYPE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "401", description = AchievementControllerConstants.UNAUTHORIZED_RESPONSE),
            @ApiResponse(responseCode = "403", description = AchievementControllerConstants.FORBIDDEN_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = AchievementControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = AchievementControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<Void>> deleteAchievementByType(@PathVariable AchievementEnums achievementType) {
        achievementService.deleteByAchievementType(achievementType);
        return success(null, MessageCodeConstant.M004_DELETED, MessageConstant.DELETED);
    }
}
