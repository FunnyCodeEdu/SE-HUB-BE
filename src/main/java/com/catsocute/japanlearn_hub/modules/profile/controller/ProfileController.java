package com.catsocute.japanlearn_hub.modules.profile.controller;

import com.catsocute.japanlearn_hub.common.constant.ApiConstant;
import com.catsocute.japanlearn_hub.common.constant.BaseFieldConstant;
import com.catsocute.japanlearn_hub.common.constant.MessageCodeConstant;
import com.catsocute.japanlearn_hub.common.constant.MessageConstant;
import com.catsocute.japanlearn_hub.common.constant.PaginationConstants;
import com.catsocute.japanlearn_hub.common.dto.MessageDTO;
import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.request.SortRequest;
import com.catsocute.japanlearn_hub.common.dto.response.GenericResponse;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.modules.profile.constant.profile.ProfileControllerConstants;
import com.catsocute.japanlearn_hub.modules.profile.dto.request.UpdateProfileRequest;
import com.catsocute.japanlearn_hub.modules.profile.dto.response.ProfileResponse;
import com.catsocute.japanlearn_hub.modules.profile.service.api.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Profile Management",
        description = "Profile management API")
@RequestMapping("/api/v1/profiles")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {
    ProfileService profileService;

    @GetMapping
    @Operation(summary = "Get all profiles",
            description = "Get list of all profiles with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ProfileControllerConstants.GET_ALL_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "400", description = ProfileControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "500", description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<PagingResponse<ProfileResponse>>> getAllProfiles(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
            ) {

        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        GenericResponse<PagingResponse<ProfileResponse>> response = GenericResponse.<PagingResponse<ProfileResponse>>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(profileService.getAllProfiles(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get my profile",
            description = "Get current user's profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ProfileControllerConstants.GET_MY_PROFILE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "401", description = ProfileControllerConstants.UNAUTHORIZED_RESPONSE),
            @ApiResponse(responseCode = "500", description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<ProfileResponse>> getMyProfile() {
        GenericResponse<ProfileResponse> response = GenericResponse.<ProfileResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(profileService.getMyProfile())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{profileId}")
    @Operation(summary = "Get profile by ID",
            description = "Get profile details by profile ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ProfileControllerConstants.GET_BY_ID_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "404", description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = "500", description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<ProfileResponse>> getProfileById(@PathVariable String profileId) {
        GenericResponse<ProfileResponse> response = GenericResponse.<ProfileResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(profileService.getProfileById(profileId))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get profile by user ID",
            description = "Get profile details by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ProfileControllerConstants.GET_BY_USER_ID_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "404", description = ProfileControllerConstants.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = "500", description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<ProfileResponse>> getProfileByUserId(@PathVariable String userId) {
        GenericResponse<ProfileResponse> response = GenericResponse.<ProfileResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M005_RETRIEVED)
                        .messageDetail(MessageConstant.RETRIEVED)
                        .build())
                .data(profileService.getDetailProfileByUserId(userId))
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping
    @Operation(summary = "Update profile",
            description = "Update current user's profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ProfileControllerConstants.UPDATE_SUCCESS_RESPONSE),
            @ApiResponse(responseCode = "400", description = ProfileControllerConstants.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "401", description = ProfileControllerConstants.UNAUTHORIZED_RESPONSE),
            @ApiResponse(responseCode = "500", description = ProfileControllerConstants.INTERNAL_ERROR_RESPONSE)
    })
    public ResponseEntity<GenericResponse<ProfileResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        GenericResponse<ProfileResponse> response = GenericResponse.<ProfileResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M003_UPDATED)
                        .messageDetail(MessageConstant.UPDATED)
                        .build())
                .data(profileService.updateProfile(request))
                .build();

        return ResponseEntity.ok(response);
    }
}
