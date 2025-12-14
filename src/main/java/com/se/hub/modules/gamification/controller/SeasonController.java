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
import com.se.hub.modules.gamification.constant.season.SeasonMessageConstants;
import com.se.hub.modules.gamification.dto.request.CreateSeasonRequest;
import com.se.hub.modules.gamification.dto.request.UpdateSeasonRequest;
import com.se.hub.modules.gamification.dto.response.SeasonResponse;
import com.se.hub.modules.gamification.service.SeasonService;
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
@Tag(name = "Season Management",
        description = "Season management API")
@RequestMapping("/seasons")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class SeasonController extends BaseController {
    SeasonService seasonService;

    @PostMapping
    @Operation(summary = "create new season",
            description = "create a new season in the system with optional rewards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200,
                    description = SeasonMessageConstants.API_SEASON_CREATED_SUCCESS,
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400,
                    description = SeasonMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500,
                    description = SeasonMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<SeasonResponse>> createSeason(@Valid @RequestBody CreateSeasonRequest request) {
        SeasonResponse seasonResponse = seasonService.createSeason(request);
        return success(seasonResponse, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }

    @GetMapping
    @Operation(summary = "get all seasons",
            description = "get list of all seasons with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200,
                    description = SeasonMessageConstants.API_SEASON_RETRIEVED_ALL_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400,
                    description = SeasonMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500,
                    description = SeasonMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<SeasonResponse>>> getAllSeasons(
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

        return success(seasonService.getAllSeasons(request),
                MessageCodeConstant.M005_RETRIEVED,
                MessageConstant.RETRIEVED);
    }

    @GetMapping("/{seasonId}")
    @Operation(summary = "get season by id",
            description = "get season information by season id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200,
                    description = SeasonMessageConstants.API_SEASON_RETRIEVED_BY_ID_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404,
                    description = GamificationMessageConstants.SEASON_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500,
                    description = SeasonMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<SeasonResponse>> getSeasonById(@PathVariable String seasonId) {
        return success(seasonService.getSeasonById(seasonId),
                MessageCodeConstant.M005_RETRIEVED,
                MessageConstant.RETRIEVED);
    }

    @PutMapping("/{seasonId}")
    @Operation(summary = "update season",
            description = "update season information by season id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200,
                    description = SeasonMessageConstants.API_SEASON_UPDATED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400,
                    description = SeasonMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404,
                    description = GamificationMessageConstants.SEASON_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500,
                    description = SeasonMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<SeasonResponse>> updateSeason(
            @PathVariable String seasonId,
            @Valid @RequestBody UpdateSeasonRequest request) {
        return success(seasonService.updateSeason(seasonId, request),
                MessageCodeConstant.M003_UPDATED,
                MessageConstant.UPDATED);
    }

    @DeleteMapping("/{seasonId}")
    @Operation(summary = "delete season",
            description = "delete a season from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200,
                    description = SeasonMessageConstants.API_SEASON_DELETED_SUCCESS,
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404,
                    description = GamificationMessageConstants.SEASON_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500,
                    description = SeasonMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<Void>> deleteSeason(@PathVariable String seasonId) {
        seasonService.deleteSeason(seasonId);
        return success(null, MessageCodeConstant.M004_DELETED, MessageConstant.DELETED);
    }
}

