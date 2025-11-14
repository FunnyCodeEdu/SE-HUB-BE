package com.se.hub.modules.chat.controller;

import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.constant.PaginationConstants;
import com.se.hub.common.constant.ResponseCode;
import com.se.hub.common.controller.BaseController;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.chat.constant.ChatMessageConstants;
import com.se.hub.modules.chat.dto.request.CreateChatMessageRequest;
import com.se.hub.modules.chat.dto.request.GetMessagesRequest;
import com.se.hub.modules.chat.dto.response.ChatMessageResponse;
import com.se.hub.modules.chat.service.api.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * Chat Message Controller
 * Handles chat message-related API endpoints
 */
@Slf4j
@Tag(name = "Chat Message Management", description = "APIs for managing chat messages")
@RequestMapping("/messages")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class ChatMessageController extends BaseController {
    
    ChatMessageService chatMessageService;
    
    @PostMapping
    @Operation(summary = "Create message", description = "Create a new chat message")
    @ApiResponses(value = {
        @ApiResponse(responseCode = ResponseCode.OK_200, description = ChatMessageConstants.API_MESSAGE_CREATED_SUCCESS),
        @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ChatMessageConstants.API_BAD_REQUEST),
        @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ChatMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<ChatMessageResponse>> createMessage(
            @Valid @RequestBody CreateChatMessageRequest request) {
        log.debug("ChatMessageController_createMessage_Creating message");
        ChatMessageResponse response = chatMessageService.createMessage(request);
        return success(response, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get messages", description = "Get messages for a conversation with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = ResponseCode.OK_200, description = ChatMessageConstants.API_MESSAGES_RETRIEVED_SUCCESS),
        @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ChatMessageConstants.API_BAD_REQUEST),
        @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ChatMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ChatMessageResponse>>> getMessages(
            @Parameter(description = "Conversation ID", required = true)
            @RequestParam("conversationId") String conversationId,
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant beforeDate) {
        log.debug("ChatMessageController_getMessages_Fetching messages for conversation: {}", conversationId);
        GetMessagesRequest request = GetMessagesRequest.builder()
            .conversationId(conversationId)
            .page(page)
            .pageSize(size)
            .beforeDate(beforeDate)
            .build();
        return success(chatMessageService.getMessages(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }
}

