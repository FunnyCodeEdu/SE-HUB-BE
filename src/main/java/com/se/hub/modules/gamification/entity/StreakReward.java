package com.se.hub.modules.gamification.entity;

import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.gamification.constant.rewardstreak.RewardStreakConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = RewardStreakConstants.TABLE_REWARD_STREAK)
public class StreakReward extends BaseEntity {

    @Min(0)
    @Column(name = RewardStreakConstants.STREAK_TARGET,
            columnDefinition = RewardStreakConstants.STREAK_TARGET_DEFINITION)
    int streakTarget;

    @Size(max = RewardStreakConstants.DESCRIPTION_MAX)
    @Column(name = RewardStreakConstants.DESCRIPTION,
            columnDefinition = RewardStreakConstants.DESCRIPTION_DEFINITION)
    String description;

    @Column(name = RewardStreakConstants.IS_ACTIVE,
            columnDefinition = RewardStreakConstants.BOOLEAN_DEFINITION)
    boolean isActive;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = RewardStreakConstants.TABLE_REWARD_STREAK_REWARD,
            joinColumns = @JoinColumn(name = RewardStreakConstants.REWARD_STREAK_ID),
            inverseJoinColumns = @JoinColumn(name = RewardStreakConstants.REWARD_ID),
            uniqueConstraints = @UniqueConstraint(columnNames = RewardStreakConstants.REWARD_ID))
    List<Reward> rewards;
}
