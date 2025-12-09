package com.se.hub.modules.gamification.service.impl;

import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.gamification.constant.gamificationprofile.GamificationProfileConstants;
import com.se.hub.modules.gamification.entity.GamificationProfile;
import com.se.hub.modules.gamification.repository.GamificationProfileRepository;
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

    @Override
    @Transactional
    public GamificationProfile createDefault(Profile profile) {
        if (profile == null) {
            throw new AppException(ErrorCode.PROFILE_NOT_NULL);
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
                    return gamificationProfileRepository.save(gamificationProfile);
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

