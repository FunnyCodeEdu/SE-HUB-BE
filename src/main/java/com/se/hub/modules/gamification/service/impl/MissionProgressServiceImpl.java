package com.se.hub.modules.gamification.service.impl;

import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.gamification.constant.missionprogress.MissionProgressConstants;
import com.se.hub.modules.gamification.dto.response.MissionProgressResponse;
import com.se.hub.modules.gamification.entity.GamificationProfile;
import com.se.hub.modules.gamification.entity.Mission;
import com.se.hub.modules.gamification.entity.MissionProgress;
import com.se.hub.modules.gamification.enums.MissionProgressStatus;
import com.se.hub.modules.gamification.enums.MissionTargetType;
import com.se.hub.modules.gamification.enums.MissionType;
import com.se.hub.modules.gamification.enums.RewardStatus;
import com.se.hub.modules.gamification.exception.GamificationErrorCode;
import com.se.hub.modules.gamification.mapper.MissionProgressMapper;
import com.se.hub.modules.gamification.repository.MissionProgressRepository;
import com.se.hub.modules.gamification.repository.MissionRepository;
import com.se.hub.modules.gamification.service.GamificationProfileService;
import com.se.hub.modules.gamification.service.MissionProgressService;
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
        
        progressList.forEach(progress -> progress.setCurrentValue(progress.getCurrentValue() + 1));
        missionProgressRepository.saveAll(progressList);
    }
}

