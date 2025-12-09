package com.se.hub.modules.gamification.service;

import com.se.hub.modules.gamification.entity.GamificationProfile;
import com.se.hub.modules.profile.entity.Profile;

public interface GamificationProfileService {
    GamificationProfile createDefault(Profile profile);
    GamificationProfile ensureGamificationProfile(Profile profile);
}

