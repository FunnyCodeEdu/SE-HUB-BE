package com.se.hub.modules.gamification.exception;

import com.se.hub.common.enums.ErrorCode;
import com.se.hub.modules.gamification.constant.common.GamificationMessageConstants;
import com.se.hub.modules.gamification.constant.common.GamificationCommonErrorCodeConstants;
import com.se.hub.modules.gamification.constant.gamificationprofile.GamificationProfileErrorCodeConstants;
import com.se.hub.modules.gamification.constant.mission.MissionErrorCodeConstants;
import com.se.hub.modules.gamification.constant.reward.RewardErrorCodeConstants;
import com.se.hub.modules.gamification.constant.rewardstreak.RewardStreakErrorCodeConstants;
import com.se.hub.modules.gamification.constant.season.SeasonErrorCodeConstants;
import com.se.hub.modules.gamification.constant.streak.StreakErrorCodeConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

@Getter
@AllArgsConstructor
public enum GamificationErrorCode {
    GAMIFICATION_PROFILE_NOT_FOUND(GamificationProfileErrorCodeConstants.GAMIFICATION_PROFILE_NOT_FOUND, GamificationMessageConstants.GAMIFICATION_PROFILE_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    PROFILE_REQUIRED(GamificationProfileErrorCodeConstants.PROFILE_REQUIRED, GamificationMessageConstants.PROFILE_REQUIRED_MESSAGE, HttpStatus.BAD_REQUEST),

    STREAK_NOT_FOUND(StreakErrorCodeConstants.STREAK_NOT_FOUND, GamificationMessageConstants.STREAK_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    MISSION_NOT_FOUND(MissionErrorCodeConstants.MISSION_NOT_FOUND, GamificationMessageConstants.MISSION_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    REWARD_NOT_FOUND(RewardErrorCodeConstants.REWARD_NOT_FOUND, GamificationMessageConstants.REWARD_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    SEASON_NOT_FOUND(SeasonErrorCodeConstants.SEASON_NOT_FOUND, GamificationMessageConstants.SEASON_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    STREAK_REWARD_NOT_FOUND(RewardStreakErrorCodeConstants.STREAK_REWARD_NOT_FOUND, GamificationMessageConstants.STREAK_REWARD_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),

    INVALID_REQUEST(GamificationCommonErrorCodeConstants.INVALID_REQUEST, GamificationMessageConstants.INVALID_REQUEST_MESSAGE, HttpStatus.BAD_REQUEST),
    FORBIDDEN_OPERATION(GamificationCommonErrorCodeConstants.FORBIDDEN_OPERATION, GamificationMessageConstants.FORBIDDEN_OPERATION_MESSAGE, HttpStatus.FORBIDDEN);

    private final String code;
    private final String messageTemplate;
    private final HttpStatus httpStatus;

    public String formatMessage(Object... args) {
        if (args == null || args.length == 0) {
            return messageTemplate;
        }
        try {
            return MessageFormat.format(messageTemplate, args);
        } catch (Exception e) {
            return messageTemplate;
        }
    }

    public ErrorCode toErrorCode() {
        return switch (this) {
            case GAMIFICATION_PROFILE_NOT_FOUND, STREAK_NOT_FOUND, MISSION_NOT_FOUND, REWARD_NOT_FOUND, SEASON_NOT_FOUND, STREAK_REWARD_NOT_FOUND ->
                    ErrorCode.DATA_NOT_FOUND;
            case PROFILE_REQUIRED, INVALID_REQUEST -> ErrorCode.DATA_INVALID;
            case FORBIDDEN_OPERATION -> ErrorCode.AUTHZ_UNAUTHORIZED;
        };
    }

    public GamificationException toException(Object... args) {
        return new GamificationException(this, args);
    }
}

