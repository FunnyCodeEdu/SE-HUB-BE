package com.se.hub.modules.chat.entity;

import com.se.hub.modules.chat.constant.ChatConstants;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

/**
 * Chat Message Entity
 * MongoDB document representing a chat message
 */
@Document(collection = ChatConstants.COLLECTION_CHAT_MESSAGE)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {
    @MongoId
    String messageId;
    
    @Indexed
    String conversationId;
    
    @Indexed
    String senderId;  // Only userId, fetch profile when needed
    
    String message;
    
    @Indexed
    Instant createDate;
}

