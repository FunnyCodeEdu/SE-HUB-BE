package com.se.hub.modules.chat.event;

import com.se.hub.modules.chat.dto.response.ChatMessageResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.List;

/**
 * Event emitted when a new chat message is created
 */
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class NewChatMessageEvent extends ApplicationEvent {
    String conversationId;
    String senderId;
    transient List<String> recipientUserIds; // All participants except sender
    transient ChatMessageResponse message;
    Instant occurredAt;

    public NewChatMessageEvent(Object source, String conversationId, String senderId, 
                               List<String> recipientUserIds, ChatMessageResponse message) {
        super(source);
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.recipientUserIds = recipientUserIds;
        this.message = message;
        this.occurredAt = Instant.now();
    }
}

