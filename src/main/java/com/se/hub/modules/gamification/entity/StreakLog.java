package com.se.hub.modules.gamification.entity;

import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.gamification.constant.gamificationprofile.GamificationProfileConstants;
import com.se.hub.modules.gamification.constant.streaklog.StreakLogConstants;
import com.se.hub.modules.gamification.constant.streaklog.StreakLogMessageConstants;
import com.se.hub.modules.gamification.enums.StreakLogStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = StreakLogConstants.TABLE_STREAK_LOG)
public class StreakLog extends BaseEntity {

    @NotNull(message = StreakLogMessageConstants.DATE_REQUIRED)
    @Column(name = StreakLogConstants.DATE,
            columnDefinition = StreakLogConstants.TIME_DEFINITION)
    Instant date;

    @Enumerated(EnumType.STRING)
    @NotNull(message = StreakLogMessageConstants.STATUS_REQUIRED)
    @Column(name = StreakLogConstants.STATUS,
            columnDefinition = StreakLogConstants.STATUS_DEFINITION)
    StreakLogStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = StreakLogConstants.GAMIFICATION_PROFILE_ID,
            referencedColumnName = GamificationProfileConstants.PROFILE_ID,
            nullable = false)
    @NotNull(message = StreakLogMessageConstants.GAMIFICATION_PROFILE_REQUIRED)
    GamificationProfile gamificationProfile;
}
