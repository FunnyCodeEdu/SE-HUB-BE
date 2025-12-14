package com.se.hub.modules.gamification.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.gamification.dto.request.CreateSeasonRequest;
import com.se.hub.modules.gamification.dto.request.UpdateSeasonRequest;
import com.se.hub.modules.gamification.dto.response.SeasonResponse;
import com.se.hub.modules.gamification.entity.Season;
import com.se.hub.modules.gamification.entity.Reward;
import com.se.hub.modules.gamification.exception.GamificationErrorCode;
import com.se.hub.modules.gamification.mapper.SeasonMapper;
import com.se.hub.modules.gamification.repository.SeasonRepository;
import com.se.hub.modules.gamification.service.RewardService;
import com.se.hub.modules.gamification.service.SeasonService;
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
public class SeasonServiceImpl implements SeasonService {

    SeasonRepository seasonRepository;
    SeasonMapper seasonMapper;
    RewardService rewardService;

    @Override
    @Transactional
    public SeasonResponse createSeason(CreateSeasonRequest request) {
        String userId = AuthUtils.getCurrentUserId();

        Season season = seasonMapper.toSeason(request);
        season.setCreatedBy(userId);
        season.setUpdateBy(userId);

        // handle rewards if provided
        if (request.getRewards() != null && !request.getRewards().isEmpty()) {
            List<Reward> rewards = rewardService.createRewards(request.getRewards(), userId);
            season.setRewards(rewards);
        }

        Season savedSeason = seasonRepository.save(season);
        return seasonMapper.toSeasonResponse(savedSeason);
    }

    @Override
    public SeasonResponse getSeasonById(String seasonId) {
        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(GamificationErrorCode.SEASON_NOT_FOUND::toException);
        return seasonMapper.toSeasonResponse(season);
    }

    @Override
    public PagingResponse<SeasonResponse> getAllSeasons(PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Season> seasonPages = seasonRepository.findAll(pageable);

        return PagingResponse.<SeasonResponse>builder()
                .currentPage(request.getPage())
                .pageSize(seasonPages.getSize())
                .totalPages(seasonPages.getTotalPages())
                .totalElement(seasonPages.getTotalElements())
                .data(seasonPages.getContent().stream()
                        .map(seasonMapper::toSeasonResponse)
                        .toList())
                .build();
    }

    @Override
    @Transactional
    public SeasonResponse updateSeason(String seasonId, UpdateSeasonRequest request) {
        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(GamificationErrorCode.SEASON_NOT_FOUND::toException);

        String userId = AuthUtils.getCurrentUserId();

        // update season fields
        seasonMapper.updateSeasonFromRequest(season, request);
        season.setUpdateBy(userId);

        // handle rewards update
        List<Reward> newRewards = rewardService.createRewards(request.getRewards(), userId);
        season.setRewards(newRewards);

        Season updatedSeason = seasonRepository.save(season);
        return seasonMapper.toSeasonResponse(updatedSeason);
    }

    @Override
    @Transactional
    public void deleteSeason(String seasonId) {
        if (seasonId == null || seasonId.isBlank()) {
            throw GamificationErrorCode.SEASON_NOT_FOUND.toException();
        }
        seasonRepository.deleteById(seasonId);
    }
}

