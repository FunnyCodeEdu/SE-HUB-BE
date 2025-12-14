package com.se.hub.modules.gamification.service;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.gamification.dto.request.CreateMissionRequest;
import com.se.hub.modules.gamification.dto.request.UpdateMissionRequest;
import com.se.hub.modules.gamification.dto.response.MissionResponse;

public interface MissionService {
    /**
     * Create a new mission with optional rewards
     * @param request Mission creation request
     * @return MissionResponse with created mission data
     * @author catsocute
     */
    MissionResponse createMission(CreateMissionRequest request);

    /**
     * Get mission by ID
     * @param missionId Mission ID
     * @return MissionResponse with mission data
     * @author catsocute
     */
    MissionResponse getMissionById(String missionId);

    /**
     * Get all missions with pagination
     * @param request Paging request
     * @return PagingResponse with list of missions
     * @author catsocute
     */
    PagingResponse<MissionResponse> getAllMissions(PagingRequest request);

    /**
     * Update mission by ID (partial update supported)
     * @param missionId Mission ID
     * @param request Update request
     * @return MissionResponse with updated mission data
     * @author catsocute
     */
    MissionResponse updateMission(String missionId, UpdateMissionRequest request);

    /**
     * Delete mission by ID
     * @param missionId Mission ID
     * @author catsocute
     */
    void deleteMission(String missionId);
}

