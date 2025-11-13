package com.catsocute.japanlearn_hub.modules.profile.service.impl;

import com.catsocute.japanlearn_hub.common.constant.GlobalVariable;
import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.common.enums.ErrorCode;
import com.catsocute.japanlearn_hub.common.exception.AppException;
import com.catsocute.japanlearn_hub.common.utils.PagingUtil;
import com.catsocute.japanlearn_hub.modules.auth.utils.AuthUtils;
import com.catsocute.japanlearn_hub.modules.profile.constant.profile.ProfileConstants;
import com.catsocute.japanlearn_hub.modules.profile.dto.request.CreateDefaultProfileRequest;
import com.catsocute.japanlearn_hub.modules.profile.dto.request.CreateUserStatsRequest;
import com.catsocute.japanlearn_hub.modules.profile.dto.request.UpdateProfileRequest;
import com.catsocute.japanlearn_hub.modules.profile.dto.response.ProfileResponse;
import com.catsocute.japanlearn_hub.modules.profile.entity.Profile;
import com.catsocute.japanlearn_hub.modules.profile.entity.UserLevel;
import com.catsocute.japanlearn_hub.modules.profile.entity.UserStats;
import com.catsocute.japanlearn_hub.modules.profile.enums.GenderEnums;
import com.catsocute.japanlearn_hub.modules.profile.enums.LevelEnums;
import com.catsocute.japanlearn_hub.modules.profile.mapper.ProfileMapper;
import com.catsocute.japanlearn_hub.modules.profile.repository.ProfileRepository;
import com.catsocute.japanlearn_hub.modules.profile.repository.UserLevelRepository;
import com.catsocute.japanlearn_hub.modules.profile.service.api.ProfileService;
import com.catsocute.japanlearn_hub.modules.profile.service.api.UserStatsService;
import com.catsocute.japanlearn_hub.modules.user.entity.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProfileServiceImpl implements ProfileService {
    ProfileRepository  profileRepository;
    UserLevelRepository userLevelRepository;
    UserStatsService userStatsService;
    ProfileMapper profileMapper;

    @Override
    public Profile createDefaultProfile(User user, CreateDefaultProfileRequest request) {
        log.info("default profile creating...");
        log.error("userId {}", user.getId());
        validateUserForProfileCreation(user.getId());
        Profile profile = buildDefaultProfile(user, request);

        Profile profileCreated = profileRepository.save(profile);

        UserStats userStats = createUserStats(profileCreated);
        profileCreated.setUserStats(userStats);

        //return
        return profileCreated;
    }

    @Override
    public ProfileResponse updateProfile(UpdateProfileRequest request) {
        Profile profile = getProfileByCurrentUser();
        profileMapper.updateProfileFromRequest(profile,request);
        return profileMapper.toProfileResponse(profileRepository.save(profile));
    }

    @Override
    public ProfileResponse getDetailProfileByUserId(String userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Cannot find profile by userId {}", userId);
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                });

        return profileMapper.toProfileResponse(profile);
    }

    @Override
    public ProfileResponse getProfileById(String profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> {
                    log.error("Cannot find profile by id {}", profileId);
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                });

        return profileMapper.toProfileResponse(profile);
    }

    @Override
    public PagingResponse<ProfileResponse> getAllProfiles(PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(
                pagingRequest.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                pagingRequest.getPageSize(),
                PagingUtil.createSort(pagingRequest));

        Page<Profile> profilePage = profileRepository.findAll(pageable);

        return PagingResponse.<ProfileResponse>builder()
                .currentPage(pagingRequest.getPage())
                .pageSize(pagingRequest.getPageSize())
                .totalPages(profilePage.getTotalPages())
                .totalElement(profilePage.getTotalElements())
                .data(profilePage.getContent().stream()
                        .map(profileMapper::toProfileResponse)
                        .toList())
                .build();
    }

    @Override
    public ProfileResponse getMyProfile() {
        Profile profile = getProfileByCurrentUser();
        log.info("profile isActive {}", profile.isActive());
        return profileMapper.toProfileResponse(profile);
    }

    private void validateUserForProfileCreation(String userId) {
        if(profileRepository.existsByUserId(userId)) {
            throw new AppException(ErrorCode.DATA_EXISTED);
        }
    }

    private UserLevel getDefaultUserLevel() {
        return userLevelRepository.findByLevel(LevelEnums.COPPER)
                .orElseThrow(() -> {
                    log.error("Cannot find default user level: {}", LevelEnums.COPPER);
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                });
    }

    private Profile buildDefaultProfile(User user, CreateDefaultProfileRequest request) {
        return Profile.builder()
                .fullName(request.getRequest().getFullName())
                .avtUrl(ProfileConstants.DEFAULT_AVT_URL)
                .gender(GenderEnums.OTHER)
                .verified(false)
                .blocked(false)
                .active(true)
                .level(getDefaultUserLevel())
                .user(user)
                .build();
    }

    private UserStats createUserStats(Profile profile) {
        CreateUserStatsRequest createUserStatsRequest = CreateUserStatsRequest.builder()
                .profile(profile)
                .build();
        return userStatsService.createUserStats(createUserStatsRequest);
    }

    private Profile getProfileByCurrentUser() {
        String currentUserId = AuthUtils.getCurrentUserId();
        return profileRepository.findByUserId(currentUserId)
                .orElseThrow(() -> {
                    log.error("Cannot find profile by userId: {}", currentUserId);
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                });
    }
}
