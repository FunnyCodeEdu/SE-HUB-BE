package com.se.hub.modules.chat.dto.request;

import com.se.hub.modules.chat.constant.ChatConstants;
import com.se.hub.modules.chat.constant.ChatErrorCodeConstants;
import com.se.hub.modules.chat.enums.ConversationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Create Conversation Request DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateConversationRequest {
    @NotNull(message = ChatErrorCodeConstants.CONVERSATION_TYPE_IS_REQUIRED)
    ConversationType type;
    
    @NotNull(message = ChatErrorCodeConstants.PARTICIPANT_ID_ARE_REQUIRED)
    @Size(min = ChatConstants.MIN_PARTICIPANTS, 
          max = ChatConstants.MAX_PARTICIPANTS,
          message = ChatErrorCodeConstants.INVALID_PARTICIPANT_COUNT)
    List<String> participantIds;
}

