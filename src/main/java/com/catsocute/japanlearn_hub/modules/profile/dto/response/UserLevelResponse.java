package com.catsocute.japanlearn_hub.modules.profile.dto.response;

import com.catsocute.japanlearn_hub.modules.profile.enums.LevelEnums;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * UserLevel response DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLevelResponse {
    String id;
    LevelEnums level;
}
