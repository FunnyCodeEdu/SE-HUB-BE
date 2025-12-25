package com.se.hub.modules.gamification.service.impl;

import com.se.hub.modules.gamification.entity.*;
import com.se.hub.modules.gamification.enums.ActionType;
import com.se.hub.modules.gamification.enums.StreakLogStatus;
import com.se.hub.modules.gamification.exception.GamificationErrorCode;
import com.se.hub.modules.gamification.repository.*;
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
    GamificationEventLogRepository  gamificationEventLogRepository;

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

        handleStreakReward(gamificationProfile, newCurrent, gamificationProfile);
    }

    private void handleStreakReward(GamificationProfile gamificationProfile, int currentStreak, GamificationProfile profile) {
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
            for (Reward reward : streakReward.getRewards()) {
                // Khởi tạo log builder
                GamificationEventLog.GamificationEventLogBuilder logBuilder = GamificationEventLog.builder()
                        .gamificationProfile(profile)
                        .actionType(ActionType.STREAK) // Dùng ActionType phù hợp cho Streak
                        .xpDelta(0L)
                        .tokenDelta(0L);

                // Logic cộng thưởng y hệt như bên Mission (nên dùng Enum RewardType b đã đưa)
                switch (reward.getRewardType()) {
                    case XP:
                        long xpValue = reward.getRewardValue();
                        profile.setTotalXp(profile.getTotalXp() + xpValue);
                        profile.setSeasonXp(profile.getSeasonXp() + xpValue);
                        logBuilder.xpDelta(xpValue);
                        break;
                    case SE_TOKEN:
                        logBuilder.tokenDelta(reward.getRewardValue());
                        break;
                    case FREEZE:
                        profile.setFreezeCount((int)(profile.getFreezeCount() + reward.getRewardValue()));
                        break;

                    case REPAIR:
                        profile.setRepairCount((int)(profile.getRepairCount() + reward.getRewardValue()));
                        break;
                }
                gamificationEventLogRepository.save(logBuilder.build());
            }
            ClaimedStreakReward claimedReward = ClaimedStreakReward.builder()
                    .claimedAt(Instant.now())
                    .gamificationProfile(gamificationProfile)
                    .streakReward(streakReward)
                    .build();
            claimedStreakRewardRepository.save(claimedReward);
        });
    }
}

