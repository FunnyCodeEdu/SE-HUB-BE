package com.se.hub.modules.interaction.controller;

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
import com.se.hub.modules.interaction.constant.InteractionMessageConstants;
import com.se.hub.modules.interaction.dto.request.CreateCommentRequest;
import com.se.hub.modules.interaction.dto.request.UpdateCommentRequest;
import com.se.hub.modules.interaction.dto.response.CommentResponse;
import com.se.hub.modules.interaction.service.api.CommentService;
import com.se.hub.modules.profile.dto.response.ProfileResponse;
import com.se.hub.modules.profile.service.api.FollowService;
import com.se.hub.modules.profile.service.api.ProfileProgressService;
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

import java.util.List;

@Slf4j
@Tag(name = "Comment Management",
        description = "Comment management API")
@RequestMapping("/comments")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class CommentController extends BaseController {
    ProfileProgressService profileProgressService;
    CommentService commentService;
    FollowService followService;

    @PostMapping
    @Operation(summary = "Create new comment",
            description = "Create a new comment in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_COMMENT_CREATED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = InteractionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<CommentResponse>> createComment(
            @Valid @RequestBody CreateCommentRequest request) {
        CommentResponse response = commentService.createComment(request);

        profileProgressService.updateCmtCount();

        return success(response, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all comments",
            description = "Get list of all comments with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_COMMENT_RETRIEVED_ALL_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = InteractionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<CommentResponse>>> getComments(
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

        return success(commentService.getComments(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/{commentId}")
    @Operation(summary = "Get comment by ID",
            description = "Get comment information by comment ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_COMMENT_RETRIEVED_BY_ID_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = InteractionMessageConstants.COMMENT_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<CommentResponse>> getCommentById(@PathVariable String commentId) {
        return success(commentService.getById(commentId), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/target/{targetType}/{targetId}")
    @Operation(summary = "Get comments by target",
            description = "Get list of all comments (including replies) for a specific target with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_COMMENT_RETRIEVED_BY_TARGET_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = InteractionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<CommentResponse>>> getCommentsByTarget(
            @PathVariable String targetType,
            @PathVariable String targetId,
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

        return success(commentService.getCommentsByTarget(targetType, targetId, request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/target/{targetType}/{targetId}/parent")
    @Operation(summary = "Get parent comments by target",
            description = "Get list of parent comments (excluding replies) for a specific target with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_COMMENT_RETRIEVED_BY_TARGET_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = InteractionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<CommentResponse>>> getParentCommentsByTarget(
            @PathVariable String targetType,
            @PathVariable String targetId,
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

        return success(commentService.getParentCommentsByTarget(targetType, targetId, request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/parent/{parentCommentId}/replies")
    @Operation(summary = "Get replies for a comment",
            description = "Get list of replies for a specific parent comment with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_COMMENT_RETRIEVED_BY_TARGET_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = InteractionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<CommentResponse>>> getRepliesByParentComment(
            @PathVariable String parentCommentId,
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.ASC) String direction
    ) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        return success(commentService.getRepliesByParentComment(parentCommentId, request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get comments by author",
            description = "Get list of comments created by a specific author with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_COMMENT_RETRIEVED_BY_TARGET_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = InteractionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<CommentResponse>>> getCommentsByAuthor(
            @PathVariable String authorId,
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

        return success(commentService.getCommentsByAuthor(authorId, request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "Update comment",
            description = "Update comment information by comment ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_COMMENT_UPDATED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = InteractionMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = InteractionMessageConstants.COMMENT_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<CommentResponse>> updateComment(
            @PathVariable String commentId,
            @Valid @RequestBody UpdateCommentRequest request) {
        return success(commentService.updateCommentById(commentId, request), MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete comment",
            description = "Delete a comment from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = InteractionMessageConstants.API_COMMENT_DELETED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = InteractionMessageConstants.COMMENT_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<Void>> deleteComment(@PathVariable String commentId) {
        commentService.deleteCommentById(commentId);
        return success(null, MessageCodeConstant.M004_DELETED, MessageConstant.DELETED);
    }

    @GetMapping("/mutual-friends")
    @Operation(summary = "Get mutual friends for tagging",
            description = "Get list of mutual friends (users that both follow each other) for tagging in comments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = "Mutual friends retrieved successfully"),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = InteractionMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<List<ProfileResponse>>> getMutualFriends() {
        log.debug("Getting mutual friends for tagging");
        List<ProfileResponse> data = followService.getMutualFriends();
        return success(data, MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }
}

