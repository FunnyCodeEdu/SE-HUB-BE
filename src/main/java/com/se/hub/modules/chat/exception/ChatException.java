package com.se.hub.modules.chat.exception;

import com.se.hub.common.exception.AppException;
import lombok.Getter;

/**
 * Custom exception for Chat module
 * Extends AppException to provide chat-specific error handling
 */
@Getter
public class ChatException extends AppException {
    private final ChatErrorCode chatErrorCode;

    public ChatException(ChatErrorCode chatErrorCode, Object... args) {
        super(chatErrorCode.toErrorCode());
        this.chatErrorCode = chatErrorCode;
    }
}

