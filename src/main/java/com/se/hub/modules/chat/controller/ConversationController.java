package com.se.hub.modules.chat.controller;

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
import com.se.hub.modules.chat.constant.ChatMessageConstants;
import com.se.hub.modules.chat.dto.request.CreateConversationRequest;
import com.se.hub.modules.chat.dto.response.ConversationResponse;
import com.se.hub.modules.chat.service.api.ConversationService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Conversation Controller
 * Handles conversation-related API endpoints
 */
@Slf4j
@Tag(name = "Conversation Management", description = "APIs for managing conversations")
@RequestMapping("/conversations")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class ConversationController extends BaseController {
    
    ConversationService conversationService;
    
    @PostMapping
    @Operation(summary = "Create conversation", description = "Create a new conversation (DIRECT or GROUP)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = ResponseCode.OK_200, description = ChatMessageConstants.API_CONVERSATION_CREATED_SUCCESS),
        @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ChatMessageConstants.API_BAD_REQUEST),
        @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ChatMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<ConversationResponse>> createConversation(
            @Valid @RequestBody CreateConversationRequest request) {
        log.debug("ConversationController_createConversation_Creating conversation");
        ConversationResponse response = conversationService.createConversation(request);
        return success(response, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }
    
    @GetMapping("/mine")
    @Operation(summary = "Get my conversations", description = "Get all conversations for current user with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = ResponseCode.OK_200, description = ChatMessageConstants.API_CONVERSATIONS_RETRIEVED_SUCCESS),
        @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ChatMessageConstants.API_BAD_REQUEST),
        @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ChatMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ConversationResponse>>> getMyConversations(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {
        log.debug("ConversationController_getMyConversations_Fetching conversations for user");
        PagingRequest request = PagingRequest.builder()
            .page(page)
            .pageSize(size)
            .sortRequest(new SortRequest(direction, field))
            .build();
        return success(conversationService.getConversations(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }
}

