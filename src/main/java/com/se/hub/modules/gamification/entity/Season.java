package com.se.hub.modules.gamification.entity;

import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.gamification.constant.season.SeasonConstants;
import com.se.hub.modules.gamification.enums.SeasonStatus;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = SeasonConstants.TABLE_SEASON)
public class Season extends BaseEntity {

    @Size(max = SeasonConstants.NAME_MAX)
    @NotNull
    @Column(name = SeasonConstants.NAME,
            columnDefinition = SeasonConstants.NAME_DEFINITION)
    String name;

    @Column(name = SeasonConstants.START_AT,
            columnDefinition = SeasonConstants.TIME_DEFINITION)
    Instant startAt;

    @Column(name = SeasonConstants.END_AT,
            columnDefinition = SeasonConstants.TIME_DEFINITION)
    Instant endAt;

    @Enumerated(EnumType.STRING)
    @Column(name = SeasonConstants.STATUS,
            columnDefinition = SeasonConstants.STATUS_DEFINITION)
    @NotNull
    SeasonStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = SeasonConstants.TABLE_SEASON_REWARD,
            joinColumns = @JoinColumn(name = SeasonConstants.SEASON_ID),
            inverseJoinColumns = @JoinColumn(name = SeasonConstants.REWARD_ID),
            uniqueConstraints = @UniqueConstraint(columnNames = SeasonConstants.REWARD_ID))
    List<Reward> rewards;
}
