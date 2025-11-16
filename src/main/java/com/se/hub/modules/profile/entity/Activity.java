package com.se.hub.modules.profile.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.profile.constant.activity.ActivityConstants;
import com.se.hub.modules.profile.constant.activity.ActivityErrorCodeConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = ActivityConstants.TABLE_ACTIVITY,
        uniqueConstraints = {
                @UniqueConstraint(
                        name = ActivityConstants.UNIQUE_CONSTRAINT_PROFILE_DATE,
                        columnNames = {ActivityConstants.COL_PROFILE_ID, ActivityConstants.COL_ACTIVITY_DATE}
                )
        })
public class Activity extends BaseEntity {

    @NotNull(message = ActivityErrorCodeConstants.ACTIVITY_PROFILE_ID_NOT_NULL)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ActivityConstants.COL_PROFILE_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Profile profile;

    @NotNull(message = ActivityErrorCodeConstants.ACTIVITY_DATE_NOT_NULL)
    @Column(name = ActivityConstants.COL_ACTIVITY_DATE,
            nullable = false)
    LocalDate activityDate;

    @Builder.Default
    @Min(value = ActivityConstants.COUNT_MIN,
            message = ActivityErrorCodeConstants.ACTIVITY_COUNT_MIN_VALUE)
    @Column(name = ActivityConstants.COL_COUNT,
            nullable = false,
            columnDefinition = ActivityConstants.COUNT_DEFINITION)
    Integer count = ActivityConstants.COUNT_DEFAULT;
}

