package com.se.hub.modules.interaction.controller;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.PaginationConstants;
import com.se.hub.common.constant.ResponseCode;
import com.se.hub.common.controller.BaseController;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.request.SortRequest;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.interaction.constant.InteractionMessageConstants;
import com.se.hub.modules.interaction.dto.response.ReactionDetailResponse;
import com.se.hub.modules.interaction.dto.response.ReactionResponse;
import com.se.hub.modules.interaction.enums.ReactionType;
import com.se.hub.modules.interaction.service.api.ReactionService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Reaction Management Controller
 * Provides APIs for managing reactions (likes, etc.) on comments, blogs, documents, and exams
 * Virtual Thread Best Practice: All endpoints run on virtual threads automatically
 * via Spring Boot global configuration. Methods return ResponseEntity directly
 * instead of CompletableFuture, making the code simpler and more performant.
 */
@Slf4j
@Tag(name = "Reaction Management",
        description = "Reaction management API")
@RequestMapping("/reactions")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class ReactionController extends BaseController {

    ReactionService reactionService;

    @PostMapping("/{targetType}/{targetId}")
    @Operation(summary = "Toggle reaction",
            description = "Toggle a reaction (like/unlike) on a target. Target types: COMMENT, BLOG, DOCUMENT, EXAM, PRACTICAL_EXAM")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_REACTION_ADDED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = InteractionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<?> toggleReaction(
            @PathVariable String targetType,
            @PathVariable String targetId,
            @RequestParam(defaultValue = "LIKE") ReactionType reactionType) {

        ReactionResponse response = reactionService.toggleReaction(targetType, targetId, reactionType);
        
        String messageDetail = response.isReacted() 
                ? InteractionMessageConstants.API_REACTION_ADDED_SUCCESS 
                : InteractionMessageConstants.API_REACTION_REMOVED_SUCCESS;

        return success(response, MessageCodeConstant.M003_UPDATED, messageDetail);
    }

    @GetMapping("/{targetType}/{targetId}/count")
    @Operation(summary = "Get reaction count",
            description = "Get the number of reactions for a target")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_REACTION_COUNT_RETRIEVED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = InteractionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<?> getReactionCount(
            @PathVariable String targetType,
            @PathVariable String targetId,
            @RequestParam(defaultValue = "LIKE") ReactionType reactionType) {

        ReactionResponse response = reactionService.getReactionCount(targetType, targetId, reactionType);
        return success(response, MessageCodeConstant.M005_RETRIEVED, InteractionMessageConstants.API_REACTION_COUNT_RETRIEVED_SUCCESS);
    }

    @GetMapping
    @Operation(summary = "Get all reactions",
            description = "Get list of all reactions with pagination. Optionally filter by target type using query parameter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_REACTION_RETRIEVED_ALL_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = InteractionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ReactionDetailResponse>>> getAllReactions(
            @RequestParam(required = false) String targetType,
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {
        log.debug("ReactionController_getAllReactions_Fetching reactions with targetType: {}, page: {}, size: {}", targetType, page, size);
        
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        if (targetType != null && !targetType.isEmpty()) {
            return success(
                    reactionService.getAllReactionsByTargetType(targetType, request),
                    MessageCodeConstant.M005_RETRIEVED,
                    InteractionMessageConstants.API_REACTION_RETRIEVED_BY_TARGET_TYPE_SUCCESS);
        } else {
            return success(
                    reactionService.getAllReactions(request),
                    MessageCodeConstant.M005_RETRIEVED,
                    InteractionMessageConstants.API_REACTION_RETRIEVED_ALL_SUCCESS);
        }
    }
}

