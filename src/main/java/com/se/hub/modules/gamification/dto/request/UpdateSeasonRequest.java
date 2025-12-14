package com.se.hub.modules.gamification.dto.request;

import com.se.hub.modules.gamification.constant.season.SeasonConstants;
import com.se.hub.modules.gamification.constant.season.SeasonMessageConstants;
import com.se.hub.modules.gamification.enums.SeasonStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateSeasonRequest {
    @Size(max = SeasonConstants.NAME_MAX, message = SeasonMessageConstants.NAME_MAX)
    String name;

    LocalDate startAt;

    LocalDate endAt;

    @Enumerated(EnumType.STRING)
    SeasonStatus status;

    @Valid
    List<CreateRewardRequest> rewards;
}

