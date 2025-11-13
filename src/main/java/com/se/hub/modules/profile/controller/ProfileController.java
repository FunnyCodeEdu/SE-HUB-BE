package com.se.hub.modules.profile.controller;

import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.controller.BaseController;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.common.dto.response.PagingResponse;
import org.springframework.http.ResponseEntity;
import com.se.hub.modules.profile.constant.profile.ProfileControllerConstants;
import com.se.hub.modules.profile.dto.request.CreateDefaultProfileRequest;
import com.se.hub.modules.profile.dto.request.UpdateProfileRequest;
import com.se.hub.modules.profile.dto.response.ProfileResponse;
import com.se.hub.modules.profile.service.api.ProfileService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = ProfileControllerConstants.TAG_NAME, description = ProfileControllerConstants.TAG_DESCRIPTION)
@RequestMapping("/profile")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
@Slf4j
public class ProfileController extends BaseController {
    ProfileService profileService;

    @PostMapping("/default")
    @Operation(summary = ProfileControllerConstants.CREATE_DEFAULT_OPERATION_SUMMARY, description = ProfileControllerConstants.CREATE_DEFAULT_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ProfileControllerConstants.CREATE_DEFAULT_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "400", description = ProfileControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "500", description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<String>> createDefaultProfile(@Valid @RequestBody CreateDefaultProfileRequest request) {
        log.info("Creating default profile for userId: {}", request.getUserId());
        profileService.createDefaultProfile(request);
        log.info("Default profile created successfully for userId: {}", request.getUserId());
        return success(MessageConstant.CREATE_DATA_SUCCESS);
    }

    @PutMapping
    @Operation(summary = ProfileControllerConstants.UPDATE_OPERATION_SUMMARY, description = ProfileControllerConstants.UPDATE_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ProfileControllerConstants.UPDATE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "400", description = ProfileControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "404", description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = "500", description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<ProfileResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        log.info("Updating profile");
        ProfileResponse data = profileService.updateProfile(request);
        log.debug("Profile updated successfully");
        return success(data);
    }

    @GetMapping
    @Operation(summary = ProfileControllerConstants.GET_ALL_OPERATION_SUMMARY, description = ProfileControllerConstants.GET_ALL_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ProfileControllerConstants.GET_ALL_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authentication required"),
            @ApiResponse(responseCode = "400", description = ProfileControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "500", description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ProfileResponse>>> getAllProfiles(PagingRequest pagingRequest) {
        log.debug("Getting all profiles with paging: page={}, size={}", pagingRequest.getPage(), pagingRequest.getPageSize());
        PagingResponse<ProfileResponse> data = profileService.getAllProfiles(pagingRequest);
        return success(data);
    }

    @GetMapping("/my-profile")
    @Operation(summary = ProfileControllerConstants.GET_MY_PROFILE_OPERATION_SUMMARY, description = ProfileControllerConstants.GET_MY_PROFILE_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ProfileControllerConstants.GET_MY_PROFILE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "404", description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = "500", description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<ProfileResponse>> getMyProfile() {
        log.debug("Getting current user's profile");
        ProfileResponse data = profileService.getMyProfile();
        return success(data);
    }

    @GetMapping("/{profileId}")
    @Operation(summary = ProfileControllerConstants.GET_BY_PROFILE_ID_OPERATION_SUMMARY, description = ProfileControllerConstants.GET_BY_PROFILE_ID_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ProfileControllerConstants.GET_BY_PROFILE_ID_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "404", description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = "500", description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<ProfileResponse>> getDetailProfileByProfileId(
            @Parameter(description = ProfileControllerConstants.PROFILE_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable @NotBlank(message = "Profile ID cannot be blank") String profileId) {
        log.debug("Getting profile by profileId: {}", profileId);
        ProfileResponse data = profileService.getDetailProfileByProfileId(profileId);
        return success(data);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = ProfileControllerConstants.GET_BY_USER_ID_OPERATION_SUMMARY, description = ProfileControllerConstants.GET_BY_USER_ID_OPERATION_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ProfileControllerConstants.GET_BY_USER_ID_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "404", description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = "500", description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<ProfileResponse>> getDetailProfileByUserId(
            @Parameter(description = ProfileControllerConstants.USER_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable @NotBlank(message = "User ID cannot be blank") String userId) {
        log.debug("Getting profile by userId: {}", userId);
        ProfileResponse data = profileService.getDetailProfileByUserId(userId);
        return success(data);
    }
}
