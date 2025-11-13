package com.se.hub.modules.profile.service.impl;

import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.profile.entity.Achievement;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.profile.repository.ProfileRepository;
import com.se.hub.modules.profile.repository.UserStatsRepository;
import com.se.hub.modules.profile.service.api.AchievementService;
import com.se.hub.modules.profile.service.api.ProfileProgressService;
import com.se.hub.modules.profile.service.api.UserLevelService;
import com.se.hub.modules.profile.service.api.UserStatsService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProfileProgressServiceImpl implements ProfileProgressService {
    UserLevelService userLevelService;
    UserStatsService userStatsService;
    AchievementService achievementService;
    UserStatsRepository userStatsRepository;
    ProfileRepository profileRepository;


    @Override
    @Transactional
    public void updatePoints(int pointDelta) {
        String userId =  getCurrentUserId();
        userStatsRepository.updateUserStats(userId, pointDelta, 0, 0, 0, 0, 0);
        updateLevel(userId);
    }

    @Override
    @Transactional
    public void updateExamsDone() {
        String userId =  getCurrentUserId();
        userStatsRepository.updateUserStats(userId, 10, 1, 0, 0, 0, 0);
        updateAchievements(userId);
    }

    @Override
    @Transactional
    public void updateCmtCount() {
        String userId =  getCurrentUserId();
        userStatsRepository.updateUserStats(userId, 5, 0, 1, 0, 0, 0);
        updateAchievements(userId);
    }

    @Override
    @Transactional
    public void updateDocsUploaded() {
        String userId =  getCurrentUserId();
        userStatsRepository.updateUserStats(userId, 15, 0, 0, 1, 0, 0);
        updateAchievements(userId);
    }

    @Override
    @Transactional
    public void updatePostsUploaded() {
        String userId =  getCurrentUserId();
        userStatsRepository.updateUserStats(userId, 15, 0, 0, 0, 1, 0);
        updateAchievements(userId);
    }

    @Override
    @Transactional
    public void updatePostShared() {
        String userId =  getCurrentUserId();
        userStatsRepository.updateUserStats(userId, 5, 0, 0, 0, 0, 1);
        updateAchievements(userId);
    }

    @Override
    @Transactional
    public void updateLevel(String userId) {
        Profile profile = getProfileByUserId(userId);
        int points = userStatsService.getUserStatsByProfileId(profile.getId()).getPoints();
        profile.setLevel(userLevelService.getUserLevelByPoints(points));
        profileRepository.save(profile);
    }

    @Override
    @Transactional
    public void updateAchievements(String userId) {
        Profile profile = getProfileByUserId(userId);
        List<Achievement> achievements = achievementService.getAchievementsByUserStats(profile.getUserStats());
        Set<Achievement> achievementSet = new HashSet<>(achievements);
        profile.setAchievements(achievementSet);
        profileRepository.save(profile);
    }

    private String getCurrentUserId() {
        return AuthUtils.getCurrentUserId();
    }

    private Profile getProfileByUserId(String userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
    }
}
