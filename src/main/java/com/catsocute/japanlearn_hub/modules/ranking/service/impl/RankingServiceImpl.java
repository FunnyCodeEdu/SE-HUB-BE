package com.catsocute.japanlearn_hub.modules.ranking.service.impl;

import com.catsocute.japanlearn_hub.common.constant.GlobalVariable;
import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.modules.profile.entity.Profile;
import com.catsocute.japanlearn_hub.modules.profile.repository.ProfileRepository;
import com.catsocute.japanlearn_hub.modules.ranking.dto.response.ProfileRankingResponse;
import com.catsocute.japanlearn_hub.modules.ranking.mapper.RankingMapper;
import com.catsocute.japanlearn_hub.modules.ranking.service.api.RankingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RankingServiceImpl implements RankingService {
    ProfileRepository profileRepository;
    RankingMapper rankingMapper;

    @Override
    public PagingResponse<ProfileRankingResponse> getRankingProfiles(PagingRequest request) {
        Sort.Direction direction = Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                Sort.by(direction, "userStats.points")
        );

        Page<Profile> profilePages = profileRepository.findAll(pageable);

        return PagingResponse.<ProfileRankingResponse>builder()
                .currentPage(request.getPage())
                .pageSize(request.getPageSize())
                .totalPages(profilePages.getTotalPages())
                .totalElement(profilePages.getTotalElements())
                .data(profilePages.getContent().stream()
                        .map(rankingMapper::toProfileRankingResponse)
                        .toList()
                )
                .build();
    }
}
