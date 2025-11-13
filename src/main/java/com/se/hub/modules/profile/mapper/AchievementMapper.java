package com.se.hub.modules.profile.mapper;

import com.se.hub.modules.profile.dto.request.CreateAchievementRequest;
import com.se.hub.modules.profile.dto.request.UpdateAchievementRequest;
import com.se.hub.modules.profile.dto.response.AchievementResponse;
import com.se.hub.modules.profile.entity.Achievement;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AchievementMapper {
    Achievement toAchievement(CreateAchievementRequest request);
    AchievementResponse toAchievementResponse(Achievement achievement);
    void updateAchievementFromRequest(UpdateAchievementRequest request, @MappingTarget Achievement achievement);
}
