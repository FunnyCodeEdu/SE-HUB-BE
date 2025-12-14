package com.se.hub.modules.gamification.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.gamification.constant.gamificationprofile.GamificationProfileConstants;
import com.se.hub.modules.gamification.constant.seasonleaderboard.SeasonLeaderBoardConstants;
import com.se.hub.modules.gamification.constant.seasonleaderboard.SeasonLeaderBoardMessageConstants;
import com.se.hub.modules.gamification.enums.RewardStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = SeasonLeaderBoardConstants.TABLE_SEASON_LEADERBOARD,
        uniqueConstraints = @UniqueConstraint(columnNames = {
                SeasonLeaderBoardConstants.SEASON_ID,
                SeasonLeaderBoardConstants.GAMIFICATION_PROFILE_ID
        }))
public class SeasonLeaderBoard extends BaseEntity {

    @NotNull(message = SeasonLeaderBoardMessageConstants.SEASON_XP_REQUIRED)
    @Min(value = 0, message = SeasonLeaderBoardMessageConstants.SEASON_XP_MIN)
    @Column(name = SeasonLeaderBoardConstants.SEASON_XP,
            columnDefinition = SeasonLeaderBoardConstants.SEASON_XP_DEFINITION)
    Long seasonXp;

    @NotNull(message = SeasonLeaderBoardMessageConstants.FINAL_RANK_REQUIRED)
    @Min(value = 1, message = SeasonLeaderBoardMessageConstants.FINAL_RANK_MIN)
    @Column(name = SeasonLeaderBoardConstants.FINAL_RANK,
            columnDefinition = SeasonLeaderBoardConstants.FINAL_RANK_DEFINITION)
    int finalRank;

    @Enumerated(EnumType.STRING)
    @NotNull(message = SeasonLeaderBoardMessageConstants.REWARD_STATUS_REQUIRED)
    @Column(name = SeasonLeaderBoardConstants.REWARD_STATUS,
            columnDefinition = SeasonLeaderBoardConstants.REWARD_STATUS_DEFINITION)
    RewardStatus rewardStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = SeasonLeaderBoardConstants.SEASON_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    @NotNull(message = SeasonLeaderBoardMessageConstants.SEASON_REQUIRED)
    Season season;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = SeasonLeaderBoardConstants.GAMIFICATION_PROFILE_ID,
            referencedColumnName = GamificationProfileConstants.PROFILE_ID,
            nullable = false)
    @NotNull(message = SeasonLeaderBoardMessageConstants.PROFILE_REQUIRED)
    GamificationProfile gamificationProfile;
}
