package com.se.hub.modules.gamification.dto.request;

import com.se.hub.modules.gamification.constant.mission.MissionMessageConstants;
import com.se.hub.modules.gamification.enums.MissionTargetType;
import com.se.hub.modules.gamification.enums.MissionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
public class UpdateMissionRequest {
    @Enumerated(EnumType.STRING)
    MissionType type;

    Boolean active;

    @Enumerated(EnumType.STRING)
    MissionTargetType targetType;

    @Min(value = 1, message = MissionMessageConstants.TOTAL_COUNT_MIN)
    Integer totalCount;

    @Size(max = 500, message = MissionMessageConstants.DESCRIPTION_MAX)
    String description;

    @Valid
    List<CreateRewardRequest> rewards;
}

