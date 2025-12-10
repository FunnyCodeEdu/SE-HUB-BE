package com.se.hub.modules.gamification.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.gamification.constant.gamificationprofile.GamificationProfileConstants;
import com.se.hub.modules.profile.entity.Profile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
    Long totalXp = GamificationProfileConstants.DEFAULT_XP;

    @Builder.Default
    @Column(name = GamificationProfileConstants.SEASON_XP,
            columnDefinition = GamificationProfileConstants.SEASON_XP_DEFINITION)
    Long seasonXp = GamificationProfileConstants.DEFAULT_XP;

    @Builder.Default
    @Column(name = GamificationProfileConstants.FREEZE_COUNT,
            columnDefinition = GamificationProfileConstants.FREEZE_COUNT_DEFINITION)
    int freezeCount = GamificationProfileConstants.DEFAULT_FREEZE_COUNT;

    @Builder.Default
    @Column(name = GamificationProfileConstants.REPAIR_COUNT,
            columnDefinition = GamificationProfileConstants.REPAIR_COUNT_DEFINITION)
    int repairCount = GamificationProfileConstants.DEFAULT_REPAIR_COUNT;

    @OneToMany(mappedBy = "gamificationProfile", fetch = FetchType.LAZY)
    List<MissionProgress> missionProgresses;

    @OneToOne(mappedBy = "gamificationProfile")
    Streak streak;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = GamificationProfileConstants.PROFILE_ID,
            referencedColumnName = GamificationProfileConstants.PROFILE_ID)
    @MapsId
    Profile profile;
}
