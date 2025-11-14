package com.se.hub.modules.chat.service.api;

import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.chat.dto.request.CreateChatMessageRequest;
import com.se.hub.modules.chat.dto.request.GetMessagesRequest;
import com.se.hub.modules.chat.dto.response.ChatMessageResponse;

/**
 * Chat Message Service Interface
 * Manages chat messages
 */
public interface ChatMessageService {
    
    /**
     * Create a new chat message
     */
    ChatMessageResponse createMessage(CreateChatMessageRequest request);
    
    /**
     * Get messages for a conversation with pagination
     */
    PagingResponse<ChatMessageResponse> getMessages(GetMessagesRequest request);
}

