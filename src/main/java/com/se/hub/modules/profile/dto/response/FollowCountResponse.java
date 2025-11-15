package com.se.hub.modules.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowCountResponse {
    private long followersCount;  // Số người follow user này
    private long followingCount;  // Số người mà user này đang follow
}

