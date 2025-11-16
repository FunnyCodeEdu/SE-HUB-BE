package com.se.hub.modules.profile.service.api;

import com.se.hub.modules.profile.dto.response.ActivityResponse;
import com.se.hub.modules.profile.dto.response.ContributionGraphResponse;

import java.time.LocalDate;

public interface ActivityService {
    /**
     * Increment activity count for a profile on current date.
     * If record doesn't exist, creates new one with count = 1.
     * If record exists, increments count by 1.
     * 
     * @param profileId Profile ID to increment activity for
     */
    void incrementActivity(String profileId);

    /**
     * Get activity count for a profile on a specific date.
     * Returns 0 if no activity found for that date.
     * 
     * @param profileId Profile ID to get activity for
     * @param date Date to get activity for
     * @return ActivityResponse with date and count
     */
    ActivityResponse getActivityByDate(String profileId, LocalDate date);

    /**
     * Get contribution graph data for a profile for a specific year.
     * Returns data in GitHub contribution graph format.
     * 
     * @param profileId Profile ID to get contribution graph for
     * @param year Year to get contribution graph for (if null, uses current year)
     * @return ContributionGraphResponse with year, total contributions, months, and color levels
     */
    ContributionGraphResponse getContributionGraph(String profileId, Integer year);
}

