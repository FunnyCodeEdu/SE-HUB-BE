package com.se.hub.modules.gamification.controller;

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
import com.se.hub.modules.gamification.constant.common.GamificationMessageConstants;
import com.se.hub.modules.gamification.constant.mission.MissionMessageConstants;
import com.se.hub.modules.gamification.dto.request.CreateMissionRequest;
import com.se.hub.modules.gamification.dto.request.UpdateMissionRequest;
import com.se.hub.modules.gamification.dto.response.MissionResponse;
import com.se.hub.modules.gamification.service.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Tag(name = "Mission Management",
        description = "Mission management API")
@RequestMapping("/missions")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class MissionController extends BaseController {
    MissionService missionService;

    @PostMapping
    @Operation(summary = "create new mission",
            description = "create a new mission in the system with optional rewards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200,
                    description = MissionMessageConstants.API_MISSION_CREATED_SUCCESS,
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400,
                    description = MissionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500,
                    description = MissionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<MissionResponse>> createMission(@Valid @RequestBody CreateMissionRequest request) {
        MissionResponse missionResponse = missionService.createMission(request);
        return success(missionResponse, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }

    @GetMapping
    @Operation(summary = "get all missions",
            description = "get list of all missions with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200,
                    description = MissionMessageConstants.API_MISSION_RETRIEVED_ALL_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400,
                    description = MissionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500,
                    description = MissionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<MissionResponse>>> getAllMissions(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false,
                    defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false,
                    defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
    ) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        return success(missionService.getAllMissions(request),
                MessageCodeConstant.M005_RETRIEVED,
                MessageConstant.RETRIEVED);
    }

    @GetMapping("/{missionId}")
    @Operation(summary = "get mission by id",
            description = "get mission information by mission id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200,
                    description = MissionMessageConstants.API_MISSION_RETRIEVED_BY_ID_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404,
                    description = GamificationMessageConstants.MISSION_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500,
                    description = MissionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<MissionResponse>> getMissionById(@PathVariable String missionId) {
        return success(missionService.getMissionById(missionId),
                MessageCodeConstant.M005_RETRIEVED,
                MessageConstant.RETRIEVED);
    }

    @PutMapping("/{missionId}")
    @Operation(summary = "update mission",
            description = "update mission information by mission id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200,
                    description = MissionMessageConstants.API_MISSION_UPDATED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400,
                    description = MissionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404,
                    description = GamificationMessageConstants.MISSION_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500,
                    description = MissionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<MissionResponse>> updateMission(
            @PathVariable String missionId,
            @Valid @RequestBody UpdateMissionRequest request) {
        return success(missionService.updateMission(missionId, request),
                MessageCodeConstant.M003_UPDATED,
                MessageConstant.UPDATED);
    }

    @DeleteMapping("/{missionId}")
    @Operation(summary = "delete mission",
            description = "delete a mission from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200,
                    description = MissionMessageConstants.API_MISSION_DELETED_SUCCESS,
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404,
                    description = GamificationMessageConstants.MISSION_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500,
                    description = MissionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<Void>> deleteMission(@PathVariable String missionId) {
        missionService.deleteMission(missionId);
        return success(null, MessageCodeConstant.M004_DELETED, MessageConstant.DELETED);
    }
}

