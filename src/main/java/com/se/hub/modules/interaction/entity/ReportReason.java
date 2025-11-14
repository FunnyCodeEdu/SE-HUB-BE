package com.se.hub.modules.interaction.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.interaction.constant.ReportConstants;
import com.se.hub.modules.interaction.enums.ReportType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = ReportConstants.TABLE_REPORT_REASON)
@Entity
public class ReportReason extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = ReportConstants.COL_REPORT_TYPE,
            nullable = false,
            columnDefinition = ReportConstants.REPORT_TYPE_DEFINITION)
    ReportType reportType;

    @Size(max = ReportConstants.DESCRIPTION_MAX_LENGTH)
    @Column(name = ReportConstants.COL_DESCRIPTION,
            columnDefinition = ReportConstants.DESCRIPTION_DEFINITION)
    String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ReportConstants.COL_REPORT_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Report report;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportReason that = (ReportReason) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}

