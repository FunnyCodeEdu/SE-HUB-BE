package com.se.hub.modules.gamification.service.impl;

import com.se.hub.modules.gamification.dto.request.CreateRewardRequest;
import com.se.hub.modules.gamification.entity.Reward;
import com.se.hub.modules.gamification.mapper.RewardMapper;
import com.se.hub.modules.gamification.repository.RewardRepository;
import com.se.hub.modules.gamification.service.RewardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RewardServiceImpl implements RewardService {

    RewardRepository rewardRepository;
    RewardMapper rewardMapper;

    @Override
    public List<Reward> createRewards(List<CreateRewardRequest> requests, String userId) {
        if (requests == null || requests.isEmpty()) {
            return new ArrayList<>();
        }

        List<Reward> rewards = new ArrayList<>();
        for (CreateRewardRequest rewardRequest : requests) {
            Reward reward = rewardMapper.toReward(rewardRequest);
            reward.setCreatedBy(userId);
            reward.setUpdateBy(userId);
            rewards.add(rewardRepository.save(reward));
        }
        return rewards;
    }
}

