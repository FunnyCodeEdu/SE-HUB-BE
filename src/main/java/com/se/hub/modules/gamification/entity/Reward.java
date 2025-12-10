package com.se.hub.modules.gamification.entity;

import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.gamification.constant.reward.RewardConstants;
import com.se.hub.modules.gamification.enums.RewardType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
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
@Table(name = RewardConstants.TABLE_REWARD)
public class Reward extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = RewardConstants.REWARD_TYPE,
            columnDefinition = RewardConstants.REWARD_TYPE_DEFINITION)
    @NotNull
    RewardType rewardType;

    @Column(name = RewardConstants.REWARD_VALUE,
            columnDefinition = RewardConstants.REWARD_VALUE_DEFINITION)
    @NotNull
    Long rewardValue;
}
