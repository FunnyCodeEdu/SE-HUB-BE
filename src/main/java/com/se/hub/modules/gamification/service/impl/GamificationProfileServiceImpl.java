package com.se.hub.modules.gamification.service.impl;

import com.se.hub.modules.gamification.constant.gamificationprofile.GamificationProfileConstants;
import com.se.hub.modules.gamification.entity.GamificationProfile;
import com.se.hub.modules.gamification.entity.Streak;
import com.se.hub.modules.gamification.exception.GamificationErrorCode;
import com.se.hub.modules.gamification.exception.GamificationException;
import com.se.hub.modules.gamification.repository.GamificationProfileRepository;
import com.se.hub.modules.gamification.repository.StreakRepository;
import com.se.hub.modules.gamification.service.GamificationProfileService;
import com.se.hub.modules.profile.entity.Profile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GamificationProfileServiceImpl implements GamificationProfileService {

    GamificationProfileRepository gamificationProfileRepository;
    StreakRepository streakRepository;

    private static final int DEFAULT_STREAK_VALUE = 0;
    private static final boolean DEFAULT_FREEZE_USED_TODAY = false;

    @Override
    @Transactional
    public GamificationProfile createDefault(Profile profile) {
        if (profile == null) {
            throw new GamificationException(GamificationErrorCode.PROFILE_REQUIRED);
        }

        String profileId = profile.getId();
        return gamificationProfileRepository.findById(profileId)
                .orElseGet(() -> {
                    GamificationProfile gamificationProfile = GamificationProfile.builder()
                            .profile(profile)
                            .totalXp(GamificationProfileConstants.DEFAULT_XP)
                            .seasonXp(GamificationProfileConstants.DEFAULT_XP)
                            .freezeCount(GamificationProfileConstants.DEFAULT_FREEZE_COUNT)
                            .repairCount(GamificationProfileConstants.DEFAULT_REPAIR_COUNT)
                            .build();
                    GamificationProfile savedProfile = gamificationProfileRepository.save(gamificationProfile);

                    Streak streak = Streak.builder()
                            .currentStreak(DEFAULT_STREAK_VALUE)
                            .maxStreak(DEFAULT_STREAK_VALUE)
                            .freezeUsedToday(DEFAULT_FREEZE_USED_TODAY)
                            .gamificationProfile(savedProfile)
                            .build();
                    Streak savedStreak = streakRepository.save(streak);
                    savedProfile.setStreak(savedStreak);

                    return savedProfile;
                });
    }

    @Override
    @Transactional
    public GamificationProfile ensureGamificationProfile(Profile profile) {
        GamificationProfile gamificationProfile = profile.getGamificationProfile();
        if (gamificationProfile != null) {
            return gamificationProfile;
        }
        GamificationProfile created = createDefault(profile);
        profile.setGamificationProfile(created);
        return created;
    }
}

