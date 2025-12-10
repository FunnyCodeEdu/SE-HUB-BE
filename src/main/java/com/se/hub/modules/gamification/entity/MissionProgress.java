package com.se.hub.modules.gamification.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.gamification.constant.gamificationprofile.GamificationProfileConstants;
import com.se.hub.modules.gamification.constant.missionprogress.MissionProgressConstants;
import com.se.hub.modules.gamification.enums.MissionProgressStatus;
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

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = MissionProgressConstants.TABLE_MISSION_PROGRESS,
        uniqueConstraints = @UniqueConstraint(columnNames = {
                MissionProgressConstants.MISSION_ID,
                MissionProgressConstants.GAMIFICATION_PROFILE_ID
        }))
public class MissionProgress extends BaseEntity {

    @Column(name = MissionProgressConstants.START_AT,
            columnDefinition = MissionProgressConstants.TIME_DEFINITION)
    Instant startAt;

    @Column(name = MissionProgressConstants.END_AT,
            columnDefinition = MissionProgressConstants.TIME_DEFINITION)
    Instant endAt;

    @NotNull
    @Min(0)
    @Column(name = MissionProgressConstants.CURRENT_VALUE,
            columnDefinition = MissionProgressConstants.CURRENT_VALUE_DEFINITION)
    int currentValue;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = MissionProgressConstants.STATUS,
            columnDefinition = MissionProgressConstants.STATUS_DEFINITION)
    MissionProgressStatus status;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = MissionProgressConstants.REWARD_STATUS,
            columnDefinition = MissionProgressConstants.REWARD_STATUS_DEFINITION)
    RewardStatus rewardStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = MissionProgressConstants.MISSION_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Mission mission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = MissionProgressConstants.GAMIFICATION_PROFILE_ID,
            referencedColumnName = GamificationProfileConstants.PROFILE_ID,
            nullable = false)
    GamificationProfile gamificationProfile;
}
