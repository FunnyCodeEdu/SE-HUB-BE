package com.se.hub.modules.gamification.exception;

import com.se.hub.common.exception.AppException;
import lombok.Getter;

@Getter
public class GamificationException extends AppException {
    private final GamificationErrorCode gamificationErrorCode;
    private final String formattedMessage;

    public GamificationException(GamificationErrorCode gamificationErrorCode, Object... args) {
        super(gamificationErrorCode.toErrorCode());
        this.gamificationErrorCode = gamificationErrorCode;
        this.formattedMessage = gamificationErrorCode.formatMessage(args);
    }
}

