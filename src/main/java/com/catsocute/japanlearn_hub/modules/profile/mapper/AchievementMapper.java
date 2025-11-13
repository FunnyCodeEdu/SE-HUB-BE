package com.catsocute.japanlearn_hub.modules.profile.mapper;

import com.catsocute.japanlearn_hub.modules.profile.dto.request.CreateAchievementRequest;
import com.catsocute.japanlearn_hub.modules.profile.dto.request.UpdateAchievementRequest;
import com.catsocute.japanlearn_hub.modules.profile.dto.response.AchievementResponse;
import com.catsocute.japanlearn_hub.modules.profile.entity.Achievement;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AchievementMapper {
    Achievement toAchievement(CreateAchievementRequest request);
    AchievementResponse toAchievementResponse(Achievement achievement);
    void updateAchievementFromRequest(UpdateAchievementRequest request, @MappingTarget Achievement achievement);
}
