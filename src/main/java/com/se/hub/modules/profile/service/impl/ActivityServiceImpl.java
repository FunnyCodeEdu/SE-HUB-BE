package com.se.hub.modules.profile.service.impl;

import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.profile.constant.activity.ActivityConstants;
import com.se.hub.modules.profile.dto.response.ActivityResponse;
import com.se.hub.modules.profile.dto.response.ContributionGraphResponse;
import com.se.hub.modules.profile.entity.Activity;
import com.se.hub.modules.profile.repository.ActivityRepository;
import com.se.hub.modules.profile.repository.ProfileRepository;
import com.se.hub.modules.profile.service.api.ActivityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Activity Service Implementation
 * Virtual Thread Best Practice:
 * - Uses synchronous blocking I/O operations (JPA repository calls)
 * - Virtual threads automatically handle blocking operations efficiently
 * - Database operations are blocking but virtual threads handle them efficiently
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActivityServiceImpl implements ActivityService {

    ActivityRepository activityRepository;
    ProfileRepository profileRepository;

    /**
     * Increment activity count for a profile on current date.
     * Virtual Thread Best Practice: Uses @Transactional with synchronous blocking I/O operations.
     * Virtual threads yield during database operations, enabling high concurrency.
     */
    @Override
    @Transactional
    public void incrementActivity(String profileId) {
        log.debug("ActivityService_incrementActivity_Incrementing activity for profile: {}", profileId);
        
        // Validate profile exists
        if (!profileRepository.existsById(profileId)) {
            log.error("ActivityService_incrementActivity_Profile not found with id: {}", profileId);
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }

        // Get current date in Vietnam timezone to avoid timezone issues
        LocalDate today = LocalDate.now(ZoneId.of(ActivityConstants.DEFAULT_TIMEZONE));
        
        // Get current user ID for audit fields
        String userId = AuthUtils.getCurrentUserId();
        
        // Blocking I/O - virtual thread yields here
        // Use upsert to increment count (creates new record if it doesn't exist, increments if exists)
        activityRepository.incrementActivityCount(profileId, today, userId);
        
        log.debug("ActivityService_incrementActivity_Activity incremented for profile {} on date {}", profileId, today);
    }

    /**
     * Get activity count for a profile on a specific date.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operation.
     * Virtual threads yield during database query, enabling high concurrency.
     */
    @Override
    public ActivityResponse getActivityByDate(String profileId, LocalDate date) {
        log.debug("ActivityService_getActivityByDate_Getting activity for profile {} on date {}", profileId, date);
        
        // Validate profile exists
        if (!profileRepository.existsById(profileId)) {
            log.error("ActivityService_getActivityByDate_Profile not found with id: {}", profileId);
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }

        // Blocking I/O - virtual thread yields here
        Activity activity = activityRepository.findByProfileIdAndActivityDate(profileId, date)
                .orElse(null);

        // Return 0 if no activity found
        Integer count = activity != null ? activity.getCount() : 0;
        
        log.debug("ActivityService_getActivityByDate_Activity count for profile {} on date {}: {}", profileId, date, count);
        
        return ActivityResponse.builder()
                .date(date)
                .count(count)
                .build();
    }

    /**
     * Get contribution graph data for a profile for a specific year.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    @Override
    public ContributionGraphResponse getContributionGraph(String profileId, Integer year) {
        log.debug("ActivityService_getContributionGraph_Getting contribution graph for profile {} year {}", profileId, year);
        
        // Validate profile exists
        if (!profileRepository.existsById(profileId)) {
            log.error("ActivityService_getContributionGraph_Profile not found with id: {}", profileId);
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }

        // Use current year if not provided
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        // Calculate date range for the year
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        // Blocking I/O - virtual thread yields here
        List<Activity> activities = activityRepository.findByProfileIdAndActivityDateBetween(profileId, startDate, endDate);

        // Create map of date -> count for quick lookup
        Map<LocalDate, Integer> activityMap = activities.stream()
                .collect(Collectors.toMap(
                        Activity::getActivityDate,
                        Activity::getCount,
                        Integer::sum
                ));

        // Calculate total contributions
        int totalContributions = activities.stream()
                .mapToInt(Activity::getCount)
                .sum();

        // Build contribution graph data
        List<ContributionGraphResponse.MonthData> months = buildMonthsData(year, activityMap);

        // Color levels (GitHub style)
        List<String> colorLevels = Arrays.asList(
                ActivityConstants.COLOR_LEVEL_0,
                ActivityConstants.COLOR_LEVEL_1,
                ActivityConstants.COLOR_LEVEL_2,
                ActivityConstants.COLOR_LEVEL_3,
                ActivityConstants.COLOR_LEVEL_4
        );

        log.debug("ActivityService_getContributionGraph_Contribution graph generated for profile {} year {}: {} total contributions", 
                profileId, year, totalContributions);

        return ContributionGraphResponse.builder()
                .year(year)
                .totalContributions(totalContributions)
                .months(months)
                .colorLevels(colorLevels)
                .build();
    }

    /**
     * Build months data with weeks for contribution graph.
     * Each month contains weeks, and each week is an array of 7 integers (Sunday to Saturday).
     * Handles leap years correctly (February with 28/29 days).
     */
    private List<ContributionGraphResponse.MonthData> buildMonthsData(int year, Map<LocalDate, Integer> activityMap) {
        List<ContributionGraphResponse.MonthData> months = new ArrayList<>();

        // Process each month
        for (int month = 1; month <= 12; month++) {
            LocalDate monthStart = LocalDate.of(year, month, 1);
            // Use lengthOfMonth() to correctly handle February (28/29 days)
            int daysInMonth = monthStart.lengthOfMonth();
            LocalDate monthEnd = LocalDate.of(year, month, daysInMonth);
            
            // Get month name abbreviation
            String monthName = monthStart.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            
            // Find the Sunday of the week containing the first day of the month
            // DayOfWeek.getValue(): Monday=1, Tuesday=2, ..., Sunday=7
            // We need to convert to: Sunday=0, Monday=1, ..., Saturday=6
            DayOfWeek monthFirstDay = monthStart.getDayOfWeek();
            int dayOfWeekValue = monthFirstDay.getValue(); // 1=Monday, 7=Sunday
            // Convert: Sunday (7) -> 0, Monday (1) -> 1, ..., Saturday (6) -> 6
            int daysToSunday = (dayOfWeekValue == 7) ? 0 : dayOfWeekValue;
            LocalDate monthWeekStart = monthStart.minusDays(daysToSunday);
            
            // Build weeks for this month
            List<List<Integer>> weeks = new ArrayList<>();
            LocalDate currentWeekStart = monthWeekStart;
            
            // Continue until we've covered all days in the month
            // Include all weeks that have at least one day in this month
            while (currentWeekStart.isBefore(monthEnd.plusDays(1))) {
                // Check if this week overlaps with the month
                LocalDate weekEnd = currentWeekStart.plusDays(6); // Saturday of this week
                if (weekEnd.isBefore(monthStart)) {
                    // This week is before the month, skip to next week
                    currentWeekStart = currentWeekStart.plusDays(7);
                    continue;
                }
                
                List<Integer> week = new ArrayList<>();
                
                // Add 7 days for this week (Sunday to Saturday)
                for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
                    LocalDate dayDate = currentWeekStart.plusDays(dayOffset);
                    
                    // Only include days within the year
                    if (dayDate.getYear() == year) {
                        Integer count = activityMap.getOrDefault(dayDate, 0);
                        week.add(count);
                    } else {
                        week.add(0);
                    }
                }
                
                weeks.add(week);
                
                // Move to next week (next Sunday)
                currentWeekStart = currentWeekStart.plusDays(7);
                
                // Stop if we've passed the month end
                if (currentWeekStart.isAfter(monthEnd)) {
                    break;
                }
            }
            
            // Only add month if it has weeks
            if (!weeks.isEmpty()) {
                months.add(ContributionGraphResponse.MonthData.builder()
                        .name(monthName)
                        .weeks(weeks)
                        .build());
            }
        }
        
        return months;
    }
}

