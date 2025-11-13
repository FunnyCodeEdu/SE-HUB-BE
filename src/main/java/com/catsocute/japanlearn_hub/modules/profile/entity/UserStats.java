package com.catsocute.japanlearn_hub.modules.profile.entity;

import com.catsocute.japanlearn_hub.common.constant.BaseFieldConstant;
import com.catsocute.japanlearn_hub.common.entity.BaseEntity;
import com.catsocute.japanlearn_hub.modules.profile.constant.profile.ProfileConstants;
import com.catsocute.japanlearn_hub.modules.profile.constant.profile.ProfileErrorCodeConstants;
import com.catsocute.japanlearn_hub.modules.profile.constant.userstats.UserStatsConstants;
import com.catsocute.japanlearn_hub.modules.profile.constant.userstats.UserStatsErrorCodeConstants;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Entity
public class UserStats extends BaseEntity {
    @Min(value = UserStatsConstants.POINTS_MIN,
            message = UserStatsErrorCodeConstants.STATS_POINTS_MIN_VALUE)
    int points;
    
    @Min(value = UserStatsConstants.EXAMS_DONE_MIN,
            message = UserStatsErrorCodeConstants.STATS_EXAMS_DONE_MIN_VALUE)
    int examsDone;
    
    @Min(value = UserStatsConstants.COMMENT_COUNT_MIN,
            message = UserStatsErrorCodeConstants.STATS_COMMENT_COUNT_MIN_VALUE)
    int cmtCount;
    
    @Min(value = UserStatsConstants.DOCS_UPLOADED_MIN,
            message = UserStatsErrorCodeConstants.STATS_DOCS_UPLOADED_MIN_VALUE)
    int docsUploaded;
    
    @Min(value = UserStatsConstants.BLOGS_UPLOADED_MIN,
            message = UserStatsErrorCodeConstants.STATS_BLOGS_UPLOADED_MIN_VALUE)
    int blogsUploaded;
    
    @Min(value = UserStatsConstants.POSTS_SHARED_MIN,
            message = UserStatsErrorCodeConstants.STATS_POSTS_SHARED_MIN_VALUE)
    int blogsShared;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ProfileConstants.PROFILE_ID, referencedColumnName = BaseFieldConstant.ID)
    @NotNull(message = ProfileErrorCodeConstants.PROFILE_NOT_NULL)
    Profile profile;
}
