package com.se.hub.modules.gamification.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.gamification.dto.request.CreateStreakRewardRequest;
import com.se.hub.modules.gamification.dto.request.UpdateStreakRewardRequest;
import com.se.hub.modules.gamification.dto.response.StreakRewardResponse;
import com.se.hub.modules.gamification.entity.Reward;
import com.se.hub.modules.gamification.entity.StreakReward;
import com.se.hub.modules.gamification.exception.GamificationErrorCode;
import com.se.hub.modules.gamification.mapper.StreakRewardMapper;
import com.se.hub.modules.gamification.repository.RewardStreakRepository;
import com.se.hub.modules.gamification.service.RewardService;
import com.se.hub.modules.gamification.service.StreakRewardService;
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
public class StreakRewardServiceImpl implements StreakRewardService {

    RewardStreakRepository rewardStreakRepository;
    StreakRewardMapper streakRewardMapper;
    RewardService rewardService;

    @Override
    @Transactional
    public StreakRewardResponse createStreakReward(CreateStreakRewardRequest request) {
        String userId = AuthUtils.getCurrentUserId();

        StreakReward streakReward = streakRewardMapper.toStreakReward(request);
        streakReward.setCreatedBy(userId);
        streakReward.setUpdateBy(userId);

        // handle rewards if provided
        if (request.getRewards() != null && !request.getRewards().isEmpty()) {
            List<Reward> rewards = rewardService.createRewards(request.getRewards(), userId);
            streakReward.setRewards(rewards);
        }

        StreakReward savedStreakReward = rewardStreakRepository.save(streakReward);
        return streakRewardMapper.toStreakRewardResponse(savedStreakReward);
    }

    @Override
    public StreakRewardResponse getStreakRewardById(String streakRewardId) {
        StreakReward streakReward = rewardStreakRepository.findById(streakRewardId)
                .orElseThrow(GamificationErrorCode.STREAK_REWARD_NOT_FOUND::toException);
        return streakRewardMapper.toStreakRewardResponse(streakReward);
    }

    @Override
    public PagingResponse<StreakRewardResponse> getAllStreakRewards(PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<StreakReward> streakRewardPages = rewardStreakRepository.findAll(pageable);

        return PagingResponse.<StreakRewardResponse>builder()
                .currentPage(request.getPage())
                .pageSize(streakRewardPages.getSize())
                .totalPages(streakRewardPages.getTotalPages())
                .totalElement(streakRewardPages.getTotalElements())
                .data(streakRewardPages.getContent().stream()
                        .map(streakRewardMapper::toStreakRewardResponse)
                        .toList())
                .build();
    }

    @Override
    @Transactional
    public StreakRewardResponse updateStreakReward(String streakRewardId, UpdateStreakRewardRequest request) {
        StreakReward streakReward = rewardStreakRepository.findById(streakRewardId)
                .orElseThrow(GamificationErrorCode.STREAK_REWARD_NOT_FOUND::toException);

        String userId = AuthUtils.getCurrentUserId();

        // update streak reward fields
        streakRewardMapper.updateStreakRewardFromRequest(streakReward, request);
        streakReward.setUpdateBy(userId);

        // handle rewards update
        List<Reward> newRewards = rewardService.createRewards(request.getRewards(), userId);
        streakReward.setRewards(newRewards);

        StreakReward updatedStreakReward = rewardStreakRepository.save(streakReward);
        return streakRewardMapper.toStreakRewardResponse(updatedStreakReward);
    }

    @Override
    @Transactional
    public void deleteStreakReward(String streakRewardId) {
        if (streakRewardId == null || streakRewardId.isBlank()) {
            throw GamificationErrorCode.STREAK_REWARD_NOT_FOUND.toException();
        }
        rewardStreakRepository.deleteById(streakRewardId);
    }
}


