package com.se.hub.modules.profile.dto.request;

import com.se.hub.modules.profile.constant.userlevel.UserLevelConstants;
import com.se.hub.modules.profile.constant.userlevel.UserLevelErrorCodeConstants;
import com.se.hub.modules.profile.enums.LevelEnums;
import jakarta.validation.constraints.Min;
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
public class UpdateUserLevelRequest {
    @NotNull(message = UserLevelErrorCodeConstants.LEVEL_NOT_NULL)
    LevelEnums level;

    @Min(value = UserLevelConstants.MIN_POINTS_MIN,
            message = UserLevelErrorCodeConstants.LEVEL_MIN_POINTS_MIN_VALUE)
    int minPoints;

    @Min(value = UserLevelConstants.MAX_POINTS_MIN,
            message = UserLevelErrorCodeConstants.LEVEL_MAX_POINTS_MIN_VALUE)
    int maxPoints;
}
