package com.se.hub.modules.gamification.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.gamification.dto.request.CreateMissionRequest;
import com.se.hub.modules.gamification.dto.request.UpdateMissionRequest;
import com.se.hub.modules.gamification.dto.response.MissionResponse;
import com.se.hub.modules.gamification.entity.Mission;
import com.se.hub.modules.gamification.entity.Reward;
import com.se.hub.modules.gamification.exception.GamificationErrorCode;
import com.se.hub.modules.gamification.mapper.MissionMapper;
import com.se.hub.modules.gamification.repository.MissionRepository;
import com.se.hub.modules.gamification.service.MissionService;
import com.se.hub.modules.gamification.service.RewardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MissionServiceImpl implements MissionService {

    MissionRepository missionRepository;
    MissionMapper missionMapper;
    RewardService rewardService;

    @Override
    @Transactional
    public MissionResponse createMission(CreateMissionRequest request) {
        String userId = AuthUtils.getCurrentUserId();

        Mission mission = missionMapper.toMission(request);
        mission.setCreatedBy(userId);
        mission.setUpdateBy(userId);

        // handle rewards if provided
        if (request.getRewards() != null && !request.getRewards().isEmpty()) {
            List<Reward> rewards = rewardService.createRewards(request.getRewards(), userId);
            mission.setRewards(rewards);
        }

        Mission savedMission = missionRepository.save(mission);
        return missionMapper.toMissionResponse(savedMission);
    }

    @Override
    public MissionResponse getMissionById(String missionId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(GamificationErrorCode.MISSION_NOT_FOUND::toException);
        return missionMapper.toMissionResponse(mission);
    }

    @Override
    public PagingResponse<MissionResponse> getAllMissions(PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Mission> missionPages = missionRepository.findAll(pageable);

        return PagingResponse.<MissionResponse>builder()
                .currentPage(request.getPage())
                .pageSize(missionPages.getSize())
                .totalPages(missionPages.getTotalPages())
                .totalElement(missionPages.getTotalElements())
                .data(missionPages.getContent().stream()
                        .map(missionMapper::toMissionResponse)
                        .toList())
                .build();
    }

    @Override
    @Transactional
    public MissionResponse updateMission(String missionId, UpdateMissionRequest request) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(GamificationErrorCode.MISSION_NOT_FOUND::toException);

        String userId = AuthUtils.getCurrentUserId();

        // update mission fields
        missionMapper.updateMissionFromRequest(mission, request);
        mission.setUpdateBy(userId);

        // handle rewards update
        List<Reward> newRewards = rewardService.createRewards(request.getRewards(), userId);
        mission.setRewards(newRewards);

        Mission updatedMission = missionRepository.save(mission);
        return missionMapper.toMissionResponse(updatedMission);
    }

    @Override
    @Transactional
    public void deleteMission(String missionId) {
        if (missionId == null || missionId.isBlank()) {
            throw GamificationErrorCode.MISSION_NOT_FOUND.toException();
        }
        missionRepository.deleteById(missionId);
    }
}

