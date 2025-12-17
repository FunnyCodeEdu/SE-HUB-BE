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
import com.se.hub.modules.gamification.constant.rewardstreak.RewardStreakMessageConstants;
import com.se.hub.modules.gamification.dto.request.CreateStreakRewardRequest;
import com.se.hub.modules.gamification.dto.request.UpdateStreakRewardRequest;
import com.se.hub.modules.gamification.dto.response.StreakRewardResponse;
import com.se.hub.modules.gamification.service.StreakRewardService;
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
@Tag(name = "Streak Reward Management",
        description = "Streak reward management API")
@RequestMapping("/streak-rewards")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class StreakRewardController extends BaseController {
    StreakRewardService streakRewardService;

    @PostMapping
    @Operation(summary = "create new streak reward",
            description = "create a new streak reward in the system with optional rewards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200,
                    description = RewardStreakMessageConstants.API_STREAK_REWARD_CREATED_SUCCESS,
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400,
                    description = RewardStreakMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500,
                    description = RewardStreakMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<StreakRewardResponse>> createStreakReward(@Valid @RequestBody CreateStreakRewardRequest request) {
        StreakRewardResponse streakRewardResponse = streakRewardService.createStreakReward(request);
        return success(streakRewardResponse, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }

    @GetMapping
    @Operation(summary = "get all streak rewards",
            description = "get list of all streak rewards with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200,
                    description = RewardStreakMessageConstants.API_STREAK_REWARD_RETRIEVED_ALL_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400,
                    description = RewardStreakMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500,
                    description = RewardStreakMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<StreakRewardResponse>>> getAllStreakRewards(
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

        return success(streakRewardService.getAllStreakRewards(request),
                MessageCodeConstant.M005_RETRIEVED,
                MessageConstant.RETRIEVED);
    }

    @GetMapping("/{streakRewardId}")
    @Operation(summary = "get streak reward by id",
            description = "get streak reward information by streak reward id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200,
                    description = RewardStreakMessageConstants.API_STREAK_REWARD_RETRIEVED_BY_ID_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404,
                    description = GamificationMessageConstants.STREAK_REWARD_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500,
                    description = RewardStreakMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<StreakRewardResponse>> getStreakRewardById(@PathVariable String streakRewardId) {
        return success(streakRewardService.getStreakRewardById(streakRewardId),
                MessageCodeConstant.M005_RETRIEVED,
                MessageConstant.RETRIEVED);
    }

    @PutMapping("/{streakRewardId}")
    @Operation(summary = "update streak reward",
            description = "update streak reward information by streak reward id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200,
                    description = RewardStreakMessageConstants.API_STREAK_REWARD_UPDATED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400,
                    description = RewardStreakMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404,
                    description = GamificationMessageConstants.STREAK_REWARD_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500,
                    description = RewardStreakMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<StreakRewardResponse>> updateStreakReward(
            @PathVariable String streakRewardId,
            @Valid @RequestBody UpdateStreakRewardRequest request) {
        return success(streakRewardService.updateStreakReward(streakRewardId, request),
                MessageCodeConstant.M003_UPDATED,
                MessageConstant.UPDATED);
    }

    @DeleteMapping("/{streakRewardId}")
    @Operation(summary = "delete streak reward",
            description = "delete a streak reward from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200,
                    description = RewardStreakMessageConstants.API_STREAK_REWARD_DELETED_SUCCESS,
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404,
                    description = GamificationMessageConstants.STREAK_REWARD_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500,
                    description = RewardStreakMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<Void>> deleteStreakReward(@PathVariable String streakRewardId) {
        streakRewardService.deleteStreakReward(streakRewardId);
        return success(null, MessageCodeConstant.M004_DELETED, MessageConstant.DELETED);
    }
}


