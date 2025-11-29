package com.se.hub.modules.profile.controller;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.constant.PaginationConstants;
import com.se.hub.common.constant.ResponseCode;
import com.se.hub.common.controller.BaseController;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.request.SortRequest;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.profile.constant.activity.ActivityControllerConstants;
import com.se.hub.modules.profile.constant.profile.ProfileControllerConstants;
import com.se.hub.modules.profile.dto.request.CreateDefaultProfileRequest;
import com.se.hub.modules.profile.dto.request.UpdateProfileRequest;
import com.se.hub.modules.profile.dto.request.UpdateSettingsRequest;
import com.se.hub.modules.profile.dto.response.ActivityResponse;
import com.se.hub.modules.profile.dto.response.CombinedSettingsResponse;
import com.se.hub.modules.profile.dto.response.ContributionGraphResponse;
import com.se.hub.modules.profile.dto.response.FollowCountResponse;
import com.se.hub.modules.profile.dto.response.ProfileResponse;
import com.se.hub.modules.profile.service.api.ActivityService;
import com.se.hub.modules.profile.service.api.FollowService;
import com.se.hub.modules.profile.service.api.ProfileProgressService;
import com.se.hub.modules.profile.service.api.ProfileService;
import com.se.hub.modules.profile.service.api.SettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = ProfileControllerConstants.TAG_NAME, description = ProfileControllerConstants.TAG_DESCRIPTION)
@RequestMapping("/profile")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
@Slf4j
public class ProfileController extends BaseController {
    ProfileService profileService;
    FollowService followService;
    ActivityService activityService;
    SettingsService settingsService;
    ProfileProgressService profileProgressService;

    @PostMapping("/default")
    @Operation(summary = ProfileControllerConstants.CREATE_DEFAULT_OPERATION_SUMMARY, description = ProfileControllerConstants.CREATE_DEFAULT_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ProfileControllerConstants.CREATE_DEFAULT_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ProfileControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<Void>> createDefaultProfile(@Valid @RequestBody CreateDefaultProfileRequest request) {
        log.info("Creating default profile for userId: {}", request.getUserId());
        profileService.createDefaultProfile(request);
        log.info("Default profile created successfully for userId: {}", request.getUserId());
        return success(null, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }

    @PutMapping
    @Operation(summary = ProfileControllerConstants.UPDATE_OPERATION_SUMMARY, description = ProfileControllerConstants.UPDATE_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ProfileControllerConstants.UPDATE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ProfileControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<ProfileResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        log.info("Updating profile");
        ProfileResponse data = profileService.updateProfile(request);
        log.debug("Profile updated successfully");
        return success(data, MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @GetMapping
    @Operation(summary = ProfileControllerConstants.GET_ALL_OPERATION_SUMMARY, description = ProfileControllerConstants.GET_ALL_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ProfileControllerConstants.GET_ALL_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authentication required"),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ProfileControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ProfileResponse>>> getAllProfiles(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
    ) {
        log.debug("Getting all profiles with paging: page={}, size={}", page, size);
        
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();
        
        PagingResponse<ProfileResponse> data = profileService.getAllProfiles(request);
        return success(data, MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/my-profile")
    @Operation(summary = ProfileControllerConstants.GET_MY_PROFILE_OPERATION_SUMMARY, description = ProfileControllerConstants.GET_MY_PROFILE_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ProfileControllerConstants.GET_MY_PROFILE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<ProfileResponse>> getMyProfile() {
        log.debug("Getting current user's profile");
        ProfileResponse data = profileService.getMyProfile();
        return success(data, MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/{profileId}")
    @Operation(summary = ProfileControllerConstants.GET_BY_PROFILE_ID_OPERATION_SUMMARY, description = ProfileControllerConstants.GET_BY_PROFILE_ID_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ProfileControllerConstants.GET_BY_PROFILE_ID_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<ProfileResponse>> getDetailProfileByProfileId(
            @Parameter(description = ProfileControllerConstants.PROFILE_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable @NotBlank(message = "Profile ID cannot be blank") String profileId) {
        log.debug("Getting profile by profileId: {}", profileId);
        ProfileResponse data = profileService.getDetailProfileByProfileId(profileId);
        return success(data, MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = ProfileControllerConstants.GET_BY_USER_ID_OPERATION_SUMMARY, description = ProfileControllerConstants.GET_BY_USER_ID_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ProfileControllerConstants.GET_BY_USER_ID_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<ProfileResponse>> getDetailProfileByUserId(
            @Parameter(description = ProfileControllerConstants.USER_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable @NotBlank(message = "User ID cannot be blank") String userId) {
        log.debug("Getting profile by userId: {}", userId);
        ProfileResponse data = profileService.getDetailProfileByUserId(userId);
        return success(data, MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @PostMapping("/follow/{userId}")
    @Operation(summary = ProfileControllerConstants.FOLLOW_USER_OPERATION_SUMMARY, description = ProfileControllerConstants.FOLLOW_USER_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ProfileControllerConstants.FOLLOW_USER_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ProfileControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<String>> followUser(
            @Parameter(description = "ID của user muốn follow", required = true)
            @PathVariable @NotBlank(message = "User ID cannot be blank") String userId) {
        log.info("Following user: {}", userId);
        followService.followUser(userId);
        log.info("Successfully followed user: {}", userId);
        return success("Follow thành công", MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }

    @DeleteMapping("/follow/{userId}")
    @Operation(summary = ProfileControllerConstants.UNFOLLOW_USER_OPERATION_SUMMARY, description = ProfileControllerConstants.UNFOLLOW_USER_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ProfileControllerConstants.UNFOLLOW_USER_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ProfileControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<String>> unfollowUser(
            @Parameter(description = "ID của user muốn unfollow", required = true)
            @PathVariable @NotBlank(message = "User ID cannot be blank") String userId) {
        log.info("Unfollowing user: {}", userId);
        followService.unfollowUser(userId);
        log.info("Successfully unfollowed user: {}", userId);
        return success("Unfollow thành công", MessageCodeConstant.M004_DELETED, MessageConstant.DELETED);
    }

    @GetMapping("/follow/{userId}/check")
    @Operation(summary = ProfileControllerConstants.CHECK_FOLLOW_OPERATION_SUMMARY, description = ProfileControllerConstants.CHECK_FOLLOW_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ProfileControllerConstants.CHECK_FOLLOW_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<Boolean>> checkFollow(
            @Parameter(description = "ID của user cần kiểm tra", required = true)
            @PathVariable @NotBlank(message = "User ID cannot be blank") String userId) {
        log.debug("Checking if following user: {}", userId);
        boolean isFollowing = followService.isFollowing(userId);
        return success(isFollowing, MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/following")
    @Operation(summary = ProfileControllerConstants.GET_FOLLOWING_OPERATION_SUMMARY, description = ProfileControllerConstants.GET_FOLLOWING_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ProfileControllerConstants.GET_FOLLOWING_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ProfileControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ProfileResponse>>> getFollowing(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size) {
        log.debug("Getting following list: page={}, size={}", page, size);
        PagingResponse<ProfileResponse> data = followService.getFollowing(page, size);
        return success(data, MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/followers")
    @Operation(summary = ProfileControllerConstants.GET_FOLLOWERS_OPERATION_SUMMARY, description = ProfileControllerConstants.GET_FOLLOWERS_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ProfileControllerConstants.GET_FOLLOWERS_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ProfileControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ProfileResponse>>> getFollowers(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size) {
        log.debug("Getting followers list: page={}, size={}", page, size);
        PagingResponse<ProfileResponse> data = followService.getFollowers(page, size);
        return success(data, MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/user/{userId}/following")
    @Operation(summary = "Get following list by user ID", description = "Get list of users that a specific user is following")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ProfileControllerConstants.GET_FOLLOWING_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ProfileControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ProfileResponse>>> getFollowingByUserId(
            @Parameter(description = ProfileControllerConstants.USER_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable @NotBlank(message = "User ID cannot be blank") String userId,
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size) {
        log.debug("Getting following list for user {}: page={}, size={}", userId, page, size);
        PagingResponse<ProfileResponse> data = followService.getFollowingByUserId(userId, page, size);
        return success(data, MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/user/{userId}/followers")
    @Operation(summary = "Get followers list by user ID", description = "Get list of users that follow a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ProfileControllerConstants.GET_FOLLOWERS_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ProfileControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ProfileResponse>>> getFollowersByUserId(
            @Parameter(description = ProfileControllerConstants.USER_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable @NotBlank(message = "User ID cannot be blank") String userId,
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size) {
        log.debug("Getting followers list for user {}: page={}, size={}", userId, page, size);
        PagingResponse<ProfileResponse> data = followService.getFollowersByUserId(userId, page, size);
        return success(data, MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/follow/count")
    @Operation(summary = "Get follow count for current user", description = "Get number of followers and following for current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = "Follow count retrieved successfully"),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<FollowCountResponse>> getFollowCount() {
        log.debug("Getting follow count for current user");
        FollowCountResponse data = followService.getFollowCount();
        return success(data, MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/user/{userId}/follow/count")
    @Operation(summary = "Get follow count by user ID", description = "Get number of followers and following for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = "Follow count retrieved successfully"),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<FollowCountResponse>> getFollowCountByUserId(
            @Parameter(description = ProfileControllerConstants.USER_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable @NotBlank(message = "User ID cannot be blank") String userId) {
        log.debug("Getting follow count for user: {}", userId);
        FollowCountResponse data = followService.getFollowCountByUserId(userId);
        return success(data, MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/{profileId}/activity")
    @Operation(summary = ActivityControllerConstants.GET_ACTIVITY_OPERATION_SUMMARY,
            description = ActivityControllerConstants.GET_ACTIVITY_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = ActivityControllerConstants.GET_ACTIVITY_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ProfileControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<ActivityResponse>> getActivityByDate(
            @Parameter(description = ActivityControllerConstants.PROFILE_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable @NotBlank(message = "Profile ID cannot be blank") String profileId,
            @Parameter(description = ActivityControllerConstants.DATE_PARAM_DESCRIPTION, required = false)
            @RequestParam(required = false) java.time.LocalDate date) {
        log.debug("Getting activity for profile {} on date {}", profileId, date);
        
        // Use current date if not provided
        if (date == null) {
            date = java.time.LocalDate.now();
        }
        
        ActivityResponse data = activityService.getActivityByDate(profileId, date);
        return success(data, MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/{profileId}/contribution-graph")
    @Operation(summary = "Get contribution graph",
            description = "Get contribution graph data for a profile in GitHub contribution graph format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = "Contribution graph retrieved successfully"),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ProfileControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<ContributionGraphResponse>> getContributionGraph(
            @Parameter(description = ActivityControllerConstants.PROFILE_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable @NotBlank(message = "Profile ID cannot be blank") String profileId,
            @Parameter(description = "Year to get contribution graph for (default: current year)", required = false)
            @RequestParam(required = false) Integer year) {
        log.debug("Getting contribution graph for profile {} year {}", profileId, year);
        
        ContributionGraphResponse data = activityService.getContributionGraph(profileId, year);
        return success(data, MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/settings")
    @Operation(summary = "Get combined settings",
            description = "Get notification and privacy settings for current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = "Settings retrieved successfully"),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<CombinedSettingsResponse>> getCombinedSettings() {
        log.debug("Getting combined settings for current user");
        CombinedSettingsResponse data = settingsService.getCombinedSettings();
        return success(data, MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @PutMapping("/settings")
    @Operation(summary = "Update settings",
            description = "Update notification and privacy settings for current user (tắt/bật nhận thông báo, trang cá nhân công khai/riêng tư, bật/ẩn email, mxh, ...)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = "Settings updated successfully"),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ProfileControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<CombinedSettingsResponse>> updateSettings(
            @Valid @RequestBody UpdateSettingsRequest request) {
        log.debug("Updating settings for current user");
        CombinedSettingsResponse data = settingsService.updateSettings(request);
        return success(data, MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @PostMapping("/sync-level")
    @Operation(summary = "Sync user level",
            description = "Recalculate and sync current user's level based on their current points")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = "User level synced successfully"),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<ProfileResponse>> syncUserLevel() {
        log.debug("Syncing user level for current user");
        String userId = com.se.hub.modules.auth.utils.AuthUtils.getCurrentUserId();
        profileProgressService.updateLevel(userId);
        ProfileResponse data = profileService.getMyProfile();
        log.info("User level synced successfully for userId: {}", userId);
        return success(data, MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }
}
