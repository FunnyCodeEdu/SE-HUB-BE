package com.se.hub.modules.chat.entity;

import com.se.hub.modules.chat.constant.ChatConstants;
import com.se.hub.modules.chat.enums.ConversationType;
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
import java.util.List;

/**
 * Conversation Entity
 * MongoDB document representing a conversation (DIRECT or GROUP)
 */
@Document(collection = ChatConstants.COLLECTION_CONVERSATION)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Conversation {
    @MongoId
    String conversationId;
    
    @Indexed
    ConversationType type;  // DIRECT or GROUP
    
    @Indexed(unique = true)
    String participantsHash;
    
    List<ParticipantInfo> participants;  // Only userIds
    
    Instant createdDate;
    Instant modifiedDate;
}

