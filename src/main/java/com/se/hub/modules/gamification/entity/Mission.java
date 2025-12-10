package com.se.hub.modules.gamification.entity;

import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.gamification.constant.mission.MissionConstants;
import com.se.hub.modules.gamification.enums.MissionTargetType;
import com.se.hub.modules.gamification.enums.MissionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@Table(name = MissionConstants.TABLE_MISSION)
public class Mission extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = MissionConstants.TYPE,
            columnDefinition = MissionConstants.TYPE_DEFINITION)
    MissionType type;

    @Column(name = MissionConstants.IS_ACTIVE)
    boolean isActive;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = MissionConstants.TARGET_TYPE,
            columnDefinition = MissionConstants.TARGET_TYPE_DEFINITION)
    MissionTargetType targetType;

    @NotNull
    @Min(1)
    @Column(name = MissionConstants.TOTAL_COUNT,
            columnDefinition = MissionConstants.TOTAL_COUNT_DEFINITION)
    int totalCount;

    @Size(max = 500)
    @Column(name = MissionConstants.DESCRIPTION,
            columnDefinition = MissionConstants.DESCRIPTION_DEFINITION)
    String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = MissionConstants.TABLE_MISSION_REWARD,
            joinColumns = @JoinColumn(name = MissionConstants.MISSION_ID),
            inverseJoinColumns = @JoinColumn(name = MissionConstants.REWARD_ID),
            uniqueConstraints = @UniqueConstraint(columnNames = MissionConstants.REWARD_ID))
    List<Reward> rewards;
}
