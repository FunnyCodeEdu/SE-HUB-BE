package com.se.hub.modules.gamification.controller;

import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.constant.ResponseCode;
import com.se.hub.common.controller.BaseController;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.modules.gamification.constant.common.GamificationMessageConstants;
import com.se.hub.modules.gamification.constant.missionprogress.MissionProgressMessageConstants;
import com.se.hub.modules.gamification.dto.response.MissionProgressResponse;
import com.se.hub.modules.gamification.service.MissionProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "Mission Progress Management",
        description = "Mission progress management API")
@RequestMapping("/mission-progress")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class MissionProgressController extends BaseController {
    MissionProgressService missionProgressService;

    @GetMapping("/daily")
    @Operation(summary = "get daily mission progress",
            description = "Get current user's daily mission progress. Creates new progress if not exists or expired.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200,
                    description = MissionProgressMessageConstants.API_DAILY_PROGRESS_RETRIEVED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400,
                    description = MissionProgressMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404,
                    description = GamificationMessageConstants.MISSION_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500,
                    description = MissionProgressMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<List<MissionProgressResponse>>> getDailyMissionProgress() {
        return success(missionProgressService.getDailyMissionProgress(),
                MessageCodeConstant.M005_RETRIEVED,
                MessageConstant.RETRIEVED);
    }
}

