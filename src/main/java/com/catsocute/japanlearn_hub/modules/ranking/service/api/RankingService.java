package com.catsocute.japanlearn_hub.modules.ranking.service.api;

import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.modules.ranking.dto.response.ProfileRankingResponse;

public interface RankingService {
    /**
     * Get ranking
     * @return Paging Profiles
     * @author catsocute
     */
    PagingResponse<ProfileRankingResponse>  getRankingProfiles(PagingRequest request);
}
