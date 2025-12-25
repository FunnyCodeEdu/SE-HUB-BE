package com.se.hub.modules.gamification.service.impl;


import com.se.hub.modules.gamification.entity.ClaimedStreakReward;
import com.se.hub.modules.gamification.entity.GamificationProfile;
import com.se.hub.modules.gamification.entity.Streak;
import com.se.hub.modules.gamification.entity.StreakLog;
import com.se.hub.modules.gamification.entity.StreakReward;
import com.se.hub.modules.gamification.enums.ActionType;
import com.se.hub.modules.gamification.enums.StreakLogStatus;
import com.se.hub.modules.gamification.exception.GamificationErrorCode;
import com.se.hub.modules.gamification.repository.ClaimedStreakRewardRepository;
import com.se.hub.modules.gamification.repository.GamificationEventLogRepository;
import com.se.hub.modules.gamification.repository.GamificationProfileRepository;
import com.se.hub.modules.gamification.repository.StreakLogRepository;
import com.se.hub.modules.gamification.repository.StreakRepository;
import com.se.hub.modules.gamification.repository.StreakRewardRepository;
import com.se.hub.modules.gamification.service.RewardService;
import com.se.hub.modules.gamification.service.StreakService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StreakServiceImpl implements StreakService {

    StreakRepository streakRepository;
    StreakLogRepository streakLogRepository;
    StreakRewardRepository streakRewardRepository;
    ClaimedStreakRewardRepository claimedStreakRewardRepository;
    GamificationProfileRepository gamificationProfileRepository;
    GamificationEventLogRepository gamificationEventLogRepository;
    RewardService  rewardService;

    @Override
    @Transactional
    public void incrementStreakAndHandleReward(String gamificationProfileId) {
        GamificationProfile gamificationProfile = gamificationProfileRepository.findById(gamificationProfileId)
                .orElseThrow(GamificationErrorCode.GAMIFICATION_PROFILE_NOT_FOUND::toException);

        Streak streak = streakRepository.findByGamificationProfileId(gamificationProfileId)
                .orElseThrow(GamificationErrorCode.STREAK_NOT_FOUND::toException);

        int newCurrent = streak.getCurrentStreak() + 1;
        streak.setCurrentStreak(newCurrent);
        if (streak.getMaxStreak() < newCurrent) {
            streak.setMaxStreak(newCurrent);
        }
        streak.setLastActiveAt(Instant.now());
        streakRepository.save(streak);

        //ghi log
        StreakLog streakLog = StreakLog.builder()
                .date(Instant.now())
                .status(StreakLogStatus.DONE)
                .gamificationProfile(gamificationProfile)
                .build();
        streakLogRepository.save(streakLog);

        handleStreakReward(gamificationProfile, newCurrent);
    }

    private void handleStreakReward(GamificationProfile gamificationProfile, int currentStreak) {
        List<StreakReward> eligibleRewards = streakRewardRepository.findByActiveTrueAndStreakTargetLessThanEqual(currentStreak);
        if (eligibleRewards.isEmpty()) {
            return;
        }

        eligibleRewards.forEach(streakReward -> {
            boolean claimed = claimedStreakRewardRepository
                    .existsByGamificationProfileIdAndStreakRewardId(gamificationProfile.getId(), streakReward.getId());
            if (claimed) {
                return;
            }
            streakReward.getRewards().forEach(
                    reward -> rewardService.handleReward(reward, gamificationProfile, ActionType.STREAK)
            );
            ClaimedStreakReward claimedReward = ClaimedStreakReward.builder()
                    .claimedAt(Instant.now())
                    .gamificationProfile(gamificationProfile)
                    .streakReward(streakReward)
                    .build();
            claimedStreakRewardRepository.save(claimedReward);
        });
    }
}

