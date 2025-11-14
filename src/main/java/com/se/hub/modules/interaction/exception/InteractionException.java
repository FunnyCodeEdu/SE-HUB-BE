package com.se.hub.modules.interaction.exception;

import com.se.hub.common.exception.AppException;
import lombok.Getter;

/**
 * Custom exception for Interaction module
 * Extends AppException to provide interaction-specific error handling
 */
@Getter
public class InteractionException extends AppException {
    private final InteractionErrorCode interactionErrorCode;

    public InteractionException(InteractionErrorCode interactionErrorCode, Object... args) {
        super(interactionErrorCode.toErrorCode());
        this.interactionErrorCode = interactionErrorCode;
    }
}

