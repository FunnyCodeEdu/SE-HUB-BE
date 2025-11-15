package com.se.hub.modules.profile.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.profile.dto.request.CreateAchievementRequest;
import com.se.hub.modules.profile.dto.request.UpdateAchievementRequest;
import com.se.hub.modules.profile.dto.response.AchievementResponse;
import com.se.hub.modules.profile.entity.Achievement;
import com.se.hub.modules.profile.entity.UserStats;
import com.se.hub.modules.profile.enums.AchievementEnums;
import com.se.hub.modules.profile.mapper.AchievementMapper;
import com.se.hub.modules.profile.repository.AchievementRepository;
import com.se.hub.modules.profile.service.api.AchievementService;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AchievementServiceImpl implements AchievementService {
    AchievementRepository achievementRepository;
    AchievementMapper achievementMapper;

    /**
     * Create a new achievement.
     * Virtual Thread Best Practice: Uses @Transactional with synchronous blocking I/O operations.
     * Virtual threads yield during each database operation, enabling high concurrency.
     */
    @Override
    @Transactional
    public AchievementResponse createAchievement(CreateAchievementRequest request) {
        log.debug("AchievementServiceImpl_createAchievement_Creating new achievement: {}", request.getAchievementType());
        
        //check for achievement existed
        if(achievementRepository.existsByAchievementType(request.getAchievementType())) {
            log.error("AchievementServiceImpl_createAchievement_Achievement type already exists: {}", request.getAchievementType());
            throw new AppException(ErrorCode.ACHIEVEMENT_TYPE_EXISTED);
        }
        Achievement achievement = achievementMapper.toAchievement(request);
        AchievementResponse response = achievementMapper.toAchievementResponse(achievementRepository.save(achievement));
        log.debug("AchievementServiceImpl_createAchievement_Achievement created successfully with id: {}", response.getId());
        return response;
    }

    @Override
    public PagingResponse<AchievementResponse> getMyAchievements(PagingRequest request) {
        return null;
    }

    /**
     * get achievements for set to profile
     *
     * @param userStats UserStats
     * @return List<Achievement>
     */
    @Override
    public List<Achievement> getAchievementsByUserStats(UserStats userStats) {
        if (userStats == null) {
            log.error("userStats is null");
            throw new AppException(ErrorCode.NOT_NULL);
        }
        List<Achievement> achievements = achievementRepository.findAll();
        return achievements.stream()
                .filter(achievement -> matchesRule(achievement, userStats))
                .toList();
    }

    @Override
    public AchievementResponse updateById(String achievementId, UpdateAchievementRequest request) {
        // check for achievement existed
        Achievement existingAchievement = getExistingAchievementById(achievementId);

        // check for new achievementType existed
        if (isNewAchievementTypeExisted(existingAchievement, request.getAchievementType())) {
            log.error("updateById achievement already exists");
            throw new AppException(ErrorCode.DATA_EXISTED);
        }
        achievementMapper.updateAchievementFromRequest(request, existingAchievement);
        return achievementMapper.toAchievementResponse(achievementRepository.save(existingAchievement));
    }

    @Override
    public AchievementResponse updateByAchievementType(AchievementEnums achievementType, UpdateAchievementRequest request) {
        Achievement existingAchievement = getExistingAchievementByType(achievementType);

        if (isNewAchievementTypeExisted(existingAchievement, request.getAchievementType())) {
            log.error("updateByAchievementType already exists");
            throw new AppException(ErrorCode.DATA_EXISTED);
        }

        achievementMapper.updateAchievementFromRequest(request, existingAchievement);
        return achievementMapper.toAchievementResponse(achievementRepository.save(existingAchievement));
    }

    @Override
    public void deleteById(String achievementId) {
        achievementRepository.deleteById(achievementId);
    }

    @Override
    public void deleteByAchievementType(AchievementEnums achievementType) {
        achievementRepository.deleteByAchievementType(achievementType);
    }

    @Override
    public PagingResponse<AchievementResponse> getAllAchievements(PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(
                pagingRequest.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                pagingRequest.getPageSize(),
                PagingUtil.createSort(pagingRequest));

        Page<Achievement> achievementPage = achievementRepository.findAll(pageable);

        return PagingResponse.<AchievementResponse>builder()
                .currentPage(achievementPage.getNumber())
                .pageSize(achievementPage.getSize())
                .totalPages(achievementPage.getTotalPages())
                .totalElement(achievementPage.getTotalElements())
                .data(achievementPage.getContent().stream()
                        .map(achievementMapper::toAchievementResponse)
                        .toList())
                .build();
    }

    @Override
    public AchievementResponse getAchievementById(String id) {
        Achievement achievement = getExistingAchievementById(id);
        return achievementMapper.toAchievementResponse(achievement);
    }

    private Achievement getExistingAchievementById(String achievementId) {
        return achievementRepository.findById(achievementId)
                .orElseThrow(() -> {
                    log.error("achievement id {} not found", achievementId);
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                });
    }

    private Achievement getExistingAchievementByType(AchievementEnums achievementType) {
        return achievementRepository.findByAchievementType(achievementType)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
    }

    private boolean matchesRule(Achievement achievement, UserStats stats) {
        return (stats.getPoints() >= achievement.getMinPoints()) &&
                (stats.getExamsDone() >= achievement.getMinExamsDone()) &&
                (stats.getCmtCount() >= achievement.getMinCmtCount()) &&
                (stats.getDocsUploaded() >= achievement.getMinDocsUploaded()) &&
                (stats.getBlogsUploaded() >= achievement.getMinBlogsUploaded()) &&
                (stats.getBlogsShared() >= achievement.getMinBlogShared());
    }

    private boolean isNewAchievementTypeExisted(Achievement existingAchievement, AchievementEnums achievementType) {
        return !existingAchievement.getAchievementType().equals(achievementType)
                && achievementRepository.existsByAchievementType(achievementType);
    }
}
