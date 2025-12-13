package com.se.hub.modules.interaction.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.interaction.constant.ReportConstants;
import com.se.hub.modules.interaction.enums.ReportStatus;
import com.se.hub.modules.interaction.enums.TargetType;
import com.se.hub.modules.profile.entity.Profile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = ReportConstants.TABLE_REPORT)
@Entity
public class Report extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ReportConstants.COL_REPORTER_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Profile reporter;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = ReportConstants.COL_TARGET_TYPE,
            nullable = false,
            columnDefinition = ReportConstants.TARGET_TYPE_DEFINITION)
    TargetType targetType;

    @NotNull
    @Column(name = ReportConstants.COL_TARGET_ID,
            nullable = false,
            columnDefinition = ReportConstants.TARGET_ID_DEFINITION)
    String targetId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = ReportConstants.COL_STATUS,
            nullable = false,
            columnDefinition = ReportConstants.STATUS_DEFINITION)
    ReportStatus status;

    @OneToMany(mappedBy = "report",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    Set<ReportReason> reasons;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return getId() != null && getId().equals(report.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}

