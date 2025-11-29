package com.se.hub.modules.chat.repository;

import com.se.hub.modules.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Chat Message Repository
 * MongoDB repository for ChatMessage entities
 */
@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    /**
     * Find messages by conversation ID ordered by create date descending with pagination
     */
    Page<ChatMessage> findByConversationIdOrderByCreateDateDesc(String conversationId, Pageable pageable);
    
    /**
     * Cursor-based pagination for real-time chat
     * Find messages by conversation ID before a specific date ordered by create date descending
     */
    List<ChatMessage> findByConversationIdAndCreateDateBeforeOrderByCreateDateDesc(
        String conversationId, Instant beforeDate, Pageable pageable);
    
    /**
     * Count messages by conversation ID
     */
    long countByConversationId(String conversationId);
    
    /**
     * Find the last (most recent) message for a conversation
     */
    ChatMessage findTopByConversationIdOrderByCreateDateDesc(String conversationId);
}

