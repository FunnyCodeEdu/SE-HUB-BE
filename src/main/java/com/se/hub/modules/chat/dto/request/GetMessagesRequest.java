package com.se.hub.modules.chat.dto.request;

import com.se.hub.modules.chat.constant.ChatErrorCodeConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * Get Messages Request DTO
 * Supports both page-based and cursor-based pagination
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetMessagesRequest {
    @NotBlank(message = ChatErrorCodeConstants.CONVERSATION_ID_IS_REQUIRED)
    String conversationId;
    
    // Pagination
    @Builder.Default
    int page = 1;
    @Builder.Default
    int pageSize = 50;
    @Builder.Default
    String sortField = "createDate";
    @Builder.Default
    String sortDirection = "DESC";
    
    // Optional: Cursor-based pagination
    Instant beforeDate;  // For cursor-based pagination
}

