package com.se.hub.modules.chat.dto.response;

import com.se.hub.modules.chat.enums.ConversationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

/**
 * Conversation Response DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse {
    String conversationId;
    ConversationType type;
    String conversationName;  // For DIRECT: other participant name, for GROUP: group name
    List<ParticipantInfoResponse> participants;  // Fetched from Profile
    Instant createdDate;
    Instant modifiedDate;
    
    // Optional fields
    ChatMessageResponse lastMessage;
    Long unreadCount;
}

