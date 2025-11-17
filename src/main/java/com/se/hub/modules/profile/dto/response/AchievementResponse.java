package com.se.hub.modules.profile.dto.response;

import com.se.hub.modules.profile.enums.AchievementEnums;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Achievement response DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AchievementResponse {
    String id;
    AchievementEnums achievementType;
    String description;
    int minPoints;
    int minExamsDone;
    int minCmtCount;
    int minDocsUploaded;
    int minBlogsUploaded;
    int minBlogShared;
}
