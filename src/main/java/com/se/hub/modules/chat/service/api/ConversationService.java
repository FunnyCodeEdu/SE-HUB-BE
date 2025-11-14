package com.se.hub.modules.chat.service.api;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.chat.dto.request.CreateConversationRequest;
import com.se.hub.modules.chat.dto.response.ConversationResponse;

/**
 * Conversation Service Interface
 * Manages conversations (DIRECT and GROUP)
 */
public interface ConversationService {
    
    /**
     * Create a new conversation
     */
    ConversationResponse createConversation(CreateConversationRequest request);
    
    /**
     * Get all conversations for current user with pagination
     */
    PagingResponse<ConversationResponse> getConversations(PagingRequest request);
}

