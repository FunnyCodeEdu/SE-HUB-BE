package com.se.hub.modules.gamification.service;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.gamification.dto.request.CreateSeasonRequest;
import com.se.hub.modules.gamification.dto.request.UpdateSeasonRequest;
import com.se.hub.modules.gamification.dto.response.SeasonResponse;

public interface SeasonService {
    /**
     * Create a new season with optional rewards
     * @param request Season creation request
     * @return SeasonResponse with created season data
     */
    SeasonResponse createSeason(CreateSeasonRequest request);

    /**
     * Get season by ID
     * @param seasonId Season ID
     * @return SeasonResponse with season data
     */
    SeasonResponse getSeasonById(String seasonId);

    /**
     * Get all seasons with pagination
     * @param request Paging request
     * @return PagingResponse with list of seasons
     */
    PagingResponse<SeasonResponse> getAllSeasons(PagingRequest request);

    /**
     * Update season by ID (partial update supported)
     * @param seasonId Season ID
     * @param request Update request
     * @return SeasonResponse with updated season data
     */
    SeasonResponse updateSeason(String seasonId, UpdateSeasonRequest request);

    /**
     * Delete season by ID
     * @param seasonId Season ID
     */
    void deleteSeason(String seasonId);
}

