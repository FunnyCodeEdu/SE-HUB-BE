package com.se.hub.modules.chat.controller;

import com.se.hub.common.controller.BaseController;
import com.se.hub.modules.chat.service.api.ChatSseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Controller for SSE (Server-Sent Events) chat subscriptions
 */
@Slf4j
@Tag(name = "SSE Chat",
        description = "Server-Sent Events API for real-time chat messages")
@RequestMapping("/chat")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatSseController extends BaseController {
    ChatSseService chatSseService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Subscribe to SSE chat messages",
            description = "Establish SSE connection for real-time chat messages. Client should use EventSource API.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SSE connection established"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public SseEmitter subscribe() {
        String userId = com.se.hub.modules.auth.utils.AuthUtils.getCurrentUserId();
        log.info("ChatSseController_subscribe_User {} subscribing to SSE chat", userId);
        return chatSseService.subscribe(userId);
    }
}

