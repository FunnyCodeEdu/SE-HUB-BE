package com.se.hub.modules.ranking.service.api;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.ranking.dto.response.ProfileRankingResponse;

public interface RankingService {
    /**
     * Get ranking
     * @return Paging Profiles
     * @author catsocute
     */
    PagingResponse<ProfileRankingResponse>  getRankingProfiles(PagingRequest request);
}
