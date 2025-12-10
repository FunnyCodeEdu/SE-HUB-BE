package com.se.hub.modules.gamification.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.gamification.constant.gamificationprofile.GamificationProfileConstants;
import com.se.hub.modules.gamification.constant.streak.StreakConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = StreakConstants.TABLE_STREAK)
public class Streak extends BaseEntity {

    @NotNull
    @Min(0)
    @Column(name = StreakConstants.CURRENT_STREAK,
            columnDefinition = StreakConstants.STREAK_INT_DEFINITION)
    Integer currentStreak;

    @NotNull
    @Min(0)
    @Column(name = StreakConstants.MAX_STREAK,
            columnDefinition = StreakConstants.STREAK_INT_DEFINITION)
    Integer maxStreak;

    @Column(name = StreakConstants.LAST_ACTIVE_AT,
            columnDefinition = StreakConstants.TIME_DEFINITION)
    Instant lastActiveAt;

    @Column(name = StreakConstants.FREEZE_USED_TODAY,
            columnDefinition = StreakConstants.BOOLEAN_DEFINITION)
    boolean freezeUsedToday;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = StreakConstants.GAMIFICATION_PROFILE_ID,
            referencedColumnName = GamificationProfileConstants.PROFILE_ID,
            nullable = false)
    GamificationProfile gamificationProfile;
}
