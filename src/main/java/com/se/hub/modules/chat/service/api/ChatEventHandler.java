package com.se.hub.modules.chat.service.api;

import com.se.hub.modules.chat.event.NewChatMessageEvent;

/**
 * Event handler for chat-related domain events
 */
public interface ChatEventHandler {
    /**
     * Handle new chat message event
     * - Send message via SSE to all participants
     * - Create notification for recipients
     */
    void handleNewChatMessage(NewChatMessageEvent event);
}

