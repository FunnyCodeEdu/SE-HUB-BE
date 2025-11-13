package com.se.hub.modules.profile.dto.request;

import com.se.hub.modules.profile.constant.achievement.AchievementConstants;
import com.se.hub.modules.profile.constant.achievement.AchievementErrorCodeConstants;
import com.se.hub.modules.profile.enums.AchievementEnums;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAchievementRequest {
    @Enumerated(EnumType.STRING)
    @NotNull(message = AchievementErrorCodeConstants.ACHIEVEMENT_TYPE_NOT_NULL)
    AchievementEnums achievementType;

    String description;

    @Min(value = AchievementConstants.MIN_EXAMS_DONE_MIN,
            message = AchievementErrorCodeConstants.ACHIEVEMENT_MIN_EXAMS_DONE_MIN_VALUE)
    int minExamsDone;

    @Min(value = AchievementConstants.MIN_POINTS_MIN,
            message = AchievementErrorCodeConstants.ACHIEVEMENT_MIN_POINTS_MIN_VALUE)
    int minPoints;

    @Min(value = AchievementConstants.MIN_CMT_COUNT_MIN,
            message = AchievementErrorCodeConstants.ACHIEVEMENT_MIN_CMT_COUNT_MIN_VALUE)
    int minCmtCount;

    @Min(value = AchievementConstants.MIN_DOCS_UPLOADED_MIN,
            message = AchievementErrorCodeConstants.ACHIEVEMENT_MIN_DOCS_UPLOADED_MIN_VALUE)
    int minDocsUploaded;

    @Min(value = AchievementConstants.MIN_BLOGS_UPLOADED_MIN,
            message = AchievementErrorCodeConstants.ACHIEVEMENT_MIN_BLOGS_UPLOADED_MIN_VALUE)
    int minBlogsUploaded;

    @Min(value = AchievementConstants.MIN_BLOG_SHARED_MIN,
            message = AchievementErrorCodeConstants.ACHIEVEMENT_MIN_BLOG_SHARED_MIN_VALUE)
    int minBlogShared;
}
