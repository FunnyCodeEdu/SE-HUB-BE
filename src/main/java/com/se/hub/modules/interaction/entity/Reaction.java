package com.se.hub.modules.interaction.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.interaction.constant.ReactionConstants;
import com.se.hub.modules.interaction.enums.ReactionType;
import com.se.hub.modules.interaction.enums.TargetType;
import com.se.hub.modules.profile.entity.Profile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = ReactionConstants.TABLE_REACTION,
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {ReactionConstants.COL_USER_ID, ReactionConstants.COL_TARGET_TYPE, ReactionConstants.COL_TARGET_ID})
        })
@Entity
public class Reaction extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ReactionConstants.COL_USER_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Profile user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = ReactionConstants.COL_TARGET_TYPE,
            nullable = false,
            columnDefinition = ReactionConstants.TARGET_TYPE_DEFINITION)
    TargetType targetType;

    @NotNull
    @Column(name = ReactionConstants.COL_TARGET_ID,
            nullable = false,
            columnDefinition = ReactionConstants.TARGET_ID_DEFINITION)
    String targetId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = ReactionConstants.COL_REACTION_TYPE,
            nullable = false,
            columnDefinition = ReactionConstants.REACTION_TYPE_DEFINITION)
    ReactionType reactionType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reaction reaction = (Reaction) o;
        return getId() != null && getId().equals(reaction.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}

