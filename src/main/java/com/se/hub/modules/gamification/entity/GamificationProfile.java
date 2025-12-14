package com.se.hub.modules.gamification.entity;

import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.gamification.constant.gamificationprofile.GamificationProfileConstants;
import com.se.hub.modules.gamification.constant.gamificationprofile.GamificationProfileMessageConstants;
import com.se.hub.modules.profile.entity.Profile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = GamificationProfileConstants.TABLE_GAMIFICATION_PROFILE)
public class GamificationProfile extends BaseEntity {

    @Builder.Default
    @Column(name = GamificationProfileConstants.TOTAL_XP,
            columnDefinition = GamificationProfileConstants.TOTAL_XP_DEFINITION)
    @NotNull(message = GamificationProfileMessageConstants.TOTAL_XP_REQUIRED)
    @Min(value = 0, message = GamificationProfileMessageConstants.TOTAL_XP_MIN)
    Long totalXp = GamificationProfileConstants.DEFAULT_XP;

    @Builder.Default
    @Column(name = GamificationProfileConstants.SEASON_XP,
            columnDefinition = GamificationProfileConstants.SEASON_XP_DEFINITION)
    @NotNull(message = GamificationProfileMessageConstants.SEASON_XP_REQUIRED)
    @Min(value = 0, message = GamificationProfileMessageConstants.SEASON_XP_MIN)
    Long seasonXp = GamificationProfileConstants.DEFAULT_XP;

    @Builder.Default
    @Column(name = GamificationProfileConstants.FREEZE_COUNT,
            columnDefinition = GamificationProfileConstants.FREEZE_COUNT_DEFINITION)
    @Min(value = 0, message = GamificationProfileMessageConstants.FREEZE_COUNT_MIN)
    int freezeCount = GamificationProfileConstants.DEFAULT_FREEZE_COUNT;

    @Builder.Default
    @Column(name = GamificationProfileConstants.REPAIR_COUNT,
            columnDefinition = GamificationProfileConstants.REPAIR_COUNT_DEFINITION)
    @Min(value = 0, message = GamificationProfileMessageConstants.REPAIR_COUNT_MIN)
    int repairCount = GamificationProfileConstants.DEFAULT_REPAIR_COUNT;

    @OneToMany(mappedBy = "gamificationProfile", fetch = FetchType.LAZY)
    List<MissionProgress> missionProgresses;

    @OneToOne(mappedBy = "gamificationProfile")
    Streak streak;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = GamificationProfileConstants.PROFILE_ID,
            referencedColumnName = GamificationProfileConstants.PROFILE_ID)
    @MapsId
    @NotNull(message = GamificationProfileMessageConstants.PROFILE_REQUIRED)
    Profile profile;
}
