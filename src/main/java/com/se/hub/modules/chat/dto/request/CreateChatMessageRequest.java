package com.se.hub.modules.chat.dto.request;

import com.se.hub.modules.chat.constant.ChatConstants;
import com.se.hub.modules.chat.constant.ChatErrorCodeConstants;
import com.se.hub.modules.chat.constant.ChatMessageConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Create Chat Message Request DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateChatMessageRequest {
    @NotBlank(message = ChatErrorCodeConstants.CONVERSATION_ID_IS_REQUIRED)
    String conversationId;
    
    @NotBlank(message = ChatErrorCodeConstants.MESSAGE_NOT_BLANK)
    @Size(max = ChatConstants.MESSAGE_MAX_LENGTH,
          message = ChatErrorCodeConstants.MESSAGE_TOO_LONG)
    String message;
}

