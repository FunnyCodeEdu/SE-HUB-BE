package com.se.hub.modules.gamification.entity;

import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.gamification.constant.eventlog.GamificationEventLogConstants;
import com.se.hub.modules.gamification.constant.eventlog.GamificationEventLogMessageConstants;
import com.se.hub.modules.gamification.constant.gamificationprofile.GamificationProfileConstants;
import com.se.hub.modules.gamification.enums.ActionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
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
@Table(name = GamificationEventLogConstants.TABLE_GAMIFICATION_EVENT_LOG)
public class GamificationEventLog extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @NotNull(message = GamificationEventLogMessageConstants.ACTION_TYPE_REQUIRED)
    @Column(name = GamificationEventLogConstants.ACTION_TYPE,
            columnDefinition = GamificationEventLogConstants.ACTION_TYPE_DEFINITION)
    ActionType actionType;

    @Column(name = GamificationEventLogConstants.XP_DELTA,
            columnDefinition = GamificationEventLogConstants.XP_DELTA_DEFINITION)
    @Min(value = 0, message = GamificationEventLogMessageConstants.XP_DELTA_MIN)
    Long xpDelta;

    @Column(name = GamificationEventLogConstants.TOKEN_DELTA,
            columnDefinition = GamificationEventLogConstants.TOKEN_DELTA_DEFINITION)
    @Min(value = 0, message = GamificationEventLogMessageConstants.TOKEN_DELTA_MIN)
    Long tokenDelta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = GamificationEventLogConstants.GAMIFICATION_PROFILE_ID,
            referencedColumnName = GamificationProfileConstants.PROFILE_ID,
            nullable = false)
    @NotNull(message = GamificationEventLogMessageConstants.GAMIFICATION_PROFILE_REQUIRED)
    GamificationProfile gamificationProfile;
}
