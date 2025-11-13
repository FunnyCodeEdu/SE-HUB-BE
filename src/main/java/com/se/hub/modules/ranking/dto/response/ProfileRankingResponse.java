package com.se.hub.modules.ranking.dto.response;

import com.se.hub.modules.profile.dto.response.UserLevelResponse;
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
public class ProfileRankingResponse {
    //profile information
    String id;
    String fullName;
    String avtUrl;
    UserLevelResponse level;

}
