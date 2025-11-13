package com.catsocute.japanlearn_hub.modules.profile.service.api;

import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.modules.profile.dto.request.CreateAchievementRequest;
import com.catsocute.japanlearn_hub.modules.profile.dto.request.UpdateAchievementRequest;
import com.catsocute.japanlearn_hub.modules.profile.dto.response.AchievementResponse;
import com.catsocute.japanlearn_hub.modules.profile.entity.Achievement;
import com.catsocute.japanlearn_hub.modules.profile.entity.UserStats;
import com.catsocute.japanlearn_hub.modules.profile.enums.AchievementEnums;

import java.util.List;

public interface AchievementService {
    /**
     * create achievement
     * @author catsocute
     * @param request CreateAchievementRequest
     * @return AchievementResponse
     */
    AchievementResponse createAchievement(CreateAchievementRequest request);

    /**
     * Get my achievements
     *
     * @author catsocute
     * @param request CreateAchievementRequest
     */
    PagingResponse<AchievementResponse> getMyAchievements(PagingRequest request);

    /**
     * Get achievements by user stats
     *
     * @author catsocute
     * @param userStats CreateAchievementRequest
     */
    List<Achievement> getAchievementsByUserStats(UserStats userStats);

    /**
     * Update achievement by id
     *
     * @author catsocute
     * @param achievementId String
     * @param request UpdateAchievementRequest
     */
    AchievementResponse updateById(String achievementId, UpdateAchievementRequest request);

    /**
     * Update achievement by achievement type
     *
     * @author catsocute
     * @param achievementType String
     * @param request UpdateAchievementRequest
     */
    AchievementResponse updateByAchievementType(AchievementEnums achievementType, UpdateAchievementRequest request);

    /**
     * Delete achievement by id
     *
     * @author catsocute
     * @param achievementId String
     */
    void deleteById(String achievementId);

    /**
     * Update achievement by id
     *
     * @author catsocute
     * @param achievementType String
     */
    void deleteByAchievementType(AchievementEnums achievementType);

    /**
     * Get all achievements with pagination
     * @author catsocute
     * @param pagingRequest PagingRequest
     * @since 9/16/2025
     */
    PagingResponse<AchievementResponse> getAllAchievements(PagingRequest pagingRequest);

    /**
     * Get achievement by ID
     * @author catsocute
     * @param id String
     * @since 9/16/2025
     */
    AchievementResponse getAchievementById(String id);
}
