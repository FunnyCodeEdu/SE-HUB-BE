package com.catsocute.japanlearn_hub.modules.profile.service.api;

import com.catsocute.japanlearn_hub.modules.profile.dto.request.CreateUserStatsRequest;
import com.catsocute.japanlearn_hub.modules.profile.entity.UserStats;

public interface UserStatsService {
    /**
     * Create user stats when create default profile
     * @author catsocute
     *
     */
    UserStats createUserStats(CreateUserStatsRequest request);

    /**
     * get user stats by profileId
     * @author catsocute
     * @param profileId String
     */
    UserStats getUserStatsByProfileId(String profileId);

    /**
     * Create user stats when create default profile
     * @author catsocute
     * @param id String
     */
    UserStats getUserStatsById(String id);

    /**
     * Create user stats when create default profile
     * @author catsocute
     * @param profileId String
     */
    UserStats resetUserStatsByProfileId(String profileId);
}
