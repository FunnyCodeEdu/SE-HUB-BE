package com.se.hub.modules.chat.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * Chat Message Response DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageResponse {
    String messageId;
    String conversationId;
    Boolean isMe;
    String message;
    ParticipantInfoResponse sender;  // Fetched from Profile
    Instant createDate;
}

