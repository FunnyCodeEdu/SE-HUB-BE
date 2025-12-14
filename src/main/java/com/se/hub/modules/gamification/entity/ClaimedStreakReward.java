package com.se.hub.modules.gamification.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.gamification.constant.claimedstreakreward.ClaimedStreakRewardConstants;
import com.se.hub.modules.gamification.constant.claimedstreakreward.ClaimedStreakRewardMessageConstants;
import com.se.hub.modules.gamification.constant.gamificationprofile.GamificationProfileConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = ClaimedStreakRewardConstants.TABLE_CLAIMED_STREAK_REWARD,
        uniqueConstraints = @UniqueConstraint(columnNames = {
                ClaimedStreakRewardConstants.GAMIFICATION_PROFILE_ID,
                ClaimedStreakRewardConstants.REWARD_STREAK_ID
        }))
public class ClaimedStreakReward extends BaseEntity {

    @Column(name = ClaimedStreakRewardConstants.CLAIMED_AT,
            columnDefinition = ClaimedStreakRewardConstants.TIME_DEFINITION)
    @NotNull(message = ClaimedStreakRewardMessageConstants.CLAIMED_AT_REQUIRED)
    Instant claimedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ClaimedStreakRewardConstants.GAMIFICATION_PROFILE_ID,
            referencedColumnName = GamificationProfileConstants.PROFILE_ID,
            nullable = false)
    @NotNull(message = ClaimedStreakRewardMessageConstants.GAMIFICATION_PROFILE_REQUIRED)
    GamificationProfile gamificationProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ClaimedStreakRewardConstants.REWARD_STREAK_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    @NotNull(message = ClaimedStreakRewardMessageConstants.REWARD_STREAK_REQUIRED)
    StreakReward streakReward;
}
