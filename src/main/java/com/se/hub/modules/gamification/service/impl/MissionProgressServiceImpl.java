package com.se.hub.modules.gamification.service.impl;

import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.gamification.constant.missionprogress.MissionProgressConstants;
import com.se.hub.modules.gamification.dto.response.MissionProgressResponse;
import com.se.hub.modules.gamification.entity.*;
import com.se.hub.modules.gamification.enums.*;
import com.se.hub.modules.gamification.exception.GamificationErrorCode;
import com.se.hub.modules.gamification.mapper.MissionProgressMapper;
import com.se.hub.modules.gamification.repository.GamificationEventLogRepository;
import com.se.hub.modules.gamification.repository.GamificationProfileRepository;
import com.se.hub.modules.gamification.repository.MissionProgressRepository;
import com.se.hub.modules.gamification.repository.MissionRepository;
import com.se.hub.modules.gamification.service.GamificationProfileService;
import com.se.hub.modules.gamification.service.MissionProgressService;
import com.se.hub.modules.gamification.service.StreakService;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.profile.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MissionProgressServiceImpl implements MissionProgressService {

    MissionProgressRepository missionProgressRepository;
    MissionRepository missionRepository;
    MissionProgressMapper missionProgressMapper;
    ProfileRepository profileRepository;
    GamificationProfileService gamificationProfileService;
    StreakService streakService;
    GamificationProfileRepository gamificationProfileRepository;
    GamificationEventLogRepository  gamificationEventLogRepository;

    @Override
    @Transactional
    public List<MissionProgressResponse> getDailyMissionProgress() {
        String userId = AuthUtils.getCurrentUserId();
        
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(GamificationErrorCode.GAMIFICATION_PROFILE_NOT_FOUND::toException);
        
        GamificationProfile gamificationProfile = gamificationProfileService.ensureGamificationProfile(profile);
        String profileId = gamificationProfile.getId();
        
        List<MissionProgress> existingProgress = missionProgressRepository.findByGamificationProfileId(profileId);
        LocalDate today = LocalDate.now();
        
        // check if existing progress is valid (startAt = today)
        if (!existingProgress.isEmpty() && existingProgress.getFirst().getStartAt().equals(today)) {
            return existingProgress.stream()
                    .map(missionProgressMapper::toMissionProgressResponse)
                    .toList();
        }
        
        // delete old record
        if (!existingProgress.isEmpty()) {
            missionProgressRepository.deleteByGamificationProfileId(profileId);
        }
        
        // get 5 daily mission
        List<Mission> randomMissions = missionRepository.findRandomByTypeAndActiveTrue(
                MissionType.DAILY.name(), 
                MissionProgressConstants.DAILY_MISSION_COUNT
        );
        
        if (randomMissions.isEmpty()) {
            throw GamificationErrorCode.MISSION_NOT_FOUND.toException();
        }
        
        // create new
        List<MissionProgress> newProgressList = randomMissions.stream()
                .map(mission -> MissionProgress.builder()
                        .mission(mission)
                        .gamificationProfile(gamificationProfile)
                        .startAt(today)
                        .endAt(today.plusDays(1))
                        .currentValue(0)
                        .status(MissionProgressStatus.IN_PROGRESS)
                        .rewardStatus(RewardStatus.PENDING)
                        .build())
                .toList();
        
        List<MissionProgress> savedProgress = missionProgressRepository.saveAll(newProgressList);
        
        return savedProgress.stream()
                .map(missionProgressMapper::toMissionProgressResponse)
                .toList();
    }

    @Override
    @Transactional
    public void updateCurrentValue(String profileId, MissionTargetType targetType) {
        List<MissionProgress> progressList = missionProgressRepository
                .findByGamificationProfileIdAndMissionTargetType(profileId, targetType);
        
        if (progressList.isEmpty()) {
            return;
        }

        progressList.forEach(progress -> {
            if (progress.getStatus() != MissionProgressStatus.COMPLETED) {
                progress.setCurrentValue(progress.getCurrentValue() + 1);
                if (isMissionCompleted(progress)) {
                    progress.setStatus(MissionProgressStatus.COMPLETED);
                    if (progress.getMission() != null) {
                        awardMissionRewards(profileId, progress.getMission().getId());
                    }
                }
            }
        });
        missionProgressRepository.saveAll(progressList);

        if (isAllDailyMissionsCompleted(profileId)) {
            streakService.incrementStreakAndHandleReward(profileId);//profileId = gamificationProfileId
        }
    }

    private boolean isMissionCompleted(MissionProgress progress) {
        Mission mission = progress.getMission();
        return mission != null && progress.getCurrentValue() >= mission.getTotalCount();
    }

    private boolean isAllDailyMissionsCompleted(String profileId) {
        LocalDate today = LocalDate.now();
        List<MissionProgress> todayProgress = missionProgressRepository
                .findByGamificationProfileIdAndStartAt(profileId, today);

        long completedDailyCount = todayProgress.stream()
                .filter(mp -> mp.getMission() != null
                        && MissionType.DAILY.equals(mp.getMission().getType()))
                .filter(mp -> MissionProgressStatus.COMPLETED.equals(mp.getStatus()))
                .count();

        return completedDailyCount >= MissionProgressConstants.DAILY_MISSION_COUNT;
    }

    private void awardMissionRewards(String profileId, String missionId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(GamificationErrorCode.MISSION_NOT_FOUND::toException);

        if (mission.getRewards() == null || mission.getRewards().isEmpty()) {
            return;
        }

        GamificationProfile profile = gamificationProfileRepository.findById(profileId)
                .orElseThrow(GamificationErrorCode.GAMIFICATION_PROFILE_NOT_FOUND::toException);

        for (Reward reward : mission.getRewards()) {
            GamificationEventLog.GamificationEventLogBuilder logBuilder = GamificationEventLog.builder()
                    .gamificationProfile(profile)
                    .actionType(ActionType.MISSION)
                    .xpDelta(0L)
                    .tokenDelta(0L);

            switch (reward.getRewardType()) {
                case XP:
                    long xpValue = reward.getRewardValue();
                    profile.setTotalXp(profile.getTotalXp() + xpValue);
                    profile.setSeasonXp(profile.getSeasonXp() + xpValue);
                    logBuilder.xpDelta(xpValue);
                    break;

                case SE_TOKEN:
                    long tokenValue =reward.getRewardValue();
                    logBuilder.tokenDelta(tokenValue);
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
        gamificationProfileRepository.save(profile);
    }

}

