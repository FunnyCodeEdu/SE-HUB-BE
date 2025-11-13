package com.se.hub.modules.ranking.controller;

import com.se.hub.common.constant.ApiConstant;
import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.dto.MessageDTO;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.ranking.dto.response.ProfileRankingResponse;
import com.se.hub.modules.ranking.service.api.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ranking Management",
        description = "Ranking management API")
@RequestMapping("/ranking")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RankingController {
    RankingService rankingService;

    @GetMapping
    @Operation(summary = "Get ranking profiles",
            description = "Get list of ranking profiles with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Server Internal Error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<ProfileRankingResponse>>> getRankingProfiles(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {

        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .build();

        GenericResponse<PagingResponse<ProfileRankingResponse>> response = GenericResponse.<PagingResponse<ProfileRankingResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(rankingService.getRankingProfiles(request))
                .build();

        return ResponseEntity.ok(response);
    }
}
