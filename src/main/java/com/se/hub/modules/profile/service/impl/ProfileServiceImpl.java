package com.se.hub.modules.profile.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.profile.constant.profile.ProfileConstants;
import com.se.hub.modules.profile.dto.request.CreateDefaultProfileRequest;
import com.se.hub.modules.profile.dto.request.CreateUserStatsRequest;
import com.se.hub.modules.profile.dto.request.UpdateProfileRequest;
import com.se.hub.modules.profile.dto.response.ProfileResponse;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.profile.entity.UserLevel;
import com.se.hub.modules.profile.entity.UserStats;
import com.se.hub.modules.profile.enums.GenderEnums;
import com.se.hub.modules.profile.enums.LevelEnums;
import com.se.hub.modules.profile.mapper.ProfileMapper;
import com.se.hub.modules.profile.repository.ProfileRepository;
import com.se.hub.modules.profile.repository.UserLevelRepository;
import com.se.hub.modules.profile.dto.response.FtesProfileResponse;
import com.se.hub.modules.profile.dto.response.FtesUserInfoResponse;
import com.se.hub.modules.profile.service.FtesProfileService;
import com.se.hub.modules.profile.service.api.ProfileService;
import com.se.hub.modules.profile.service.api.UserStatsService;
import com.se.hub.modules.user.entity.User;
import com.se.hub.modules.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProfileServiceImpl implements ProfileService {
    ProfileRepository  profileRepository;
    UserRepository userRepository;
    UserLevelRepository userLevelRepository;
    UserStatsService userStatsService;
    ProfileMapper profileMapper;
    FtesProfileService ftesProfileService;

    @Override
    @Transactional
    public void createDefaultProfile(CreateDefaultProfileRequest request) {
        log.info("Creating default profile for userId: {}", request.getUserId());
        
        // Load user from database
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found with userId: {}", request.getUserId());
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                });
        
        // Validation: Check if user is current user
        String currentUserId = AuthUtils.getCurrentUserId();
        String userId = user.getId();
        
        if (userId == null || !userId.equals(currentUserId)) {
            log.warn("User {} attempted to create profile for different user {}", currentUserId, userId);
            throw new AppException(ErrorCode.DATA_NOT_FOUND);
        }
        
        // Check if profile already exists
        if (profileRepository.existsByUserId(userId)) {
            log.warn("Profile already exists for userId: {}", userId);
            throw new AppException(ErrorCode.DATA_EXISTED);
        }
        
        // Get default user level (COPPER)
        UserLevel userLevel = userLevelRepository.findByLevel(LevelEnums.COPPER)
                .orElseThrow(() -> {
                    log.error("Cannot find default user level: {}", LevelEnums.COPPER);
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                });
        
        // Build default profile
        Profile profile = Profile.builder()
                .avtUrl(ProfileConstants.DEFAULT_AVT_URL)
                .fullName(null) // Null is allowed by validation, empty string is not
                .email(null) // Email will be set later by user if needed
                .gender(GenderEnums.OTHER)
                .isVerified(false)
                .isBlocked(false)
                .isActive(true)
                .level(userLevel)
                .user(user)
                .build();
        
        // Normalize empty strings to null before saving to avoid validation errors
        normalizeProfileStrings(profile);
        
        // Save profile first to generate ID
        profile = profileRepository.save(profile);
        
        // Create user stats with saved profile
        CreateUserStatsRequest createUserStatsRequest = CreateUserStatsRequest.builder()
                .profile(profile)
                .build();
        UserStats userStats = userStatsService.createUserStats(createUserStatsRequest);
        
        // Set user stats to profile
        profile.setUserStats(userStats);
        
        log.info("Default profile created successfully for userId: {}", userId);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(UpdateProfileRequest request) {
        log.info("Updating profile for current user");
        String currentUserId = AuthUtils.getCurrentUserId();
        
        // Get or create profile for current user
        Profile profile = profileRepository.findByUserId(currentUserId)
                .orElseGet(() -> {
                    log.info("Profile not found for current user {}. Creating new profile before update.", currentUserId);
                    User user = userRepository.findById(currentUserId)
                            .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
                    return createDefaultProfileIfNotExists(user);
                });
        
        // Update profile from request
        profileMapper.updateProfileFromRequest(profile, request);
        
        // Normalize empty strings to null before saving to avoid validation errors
        normalizeProfileStrings(profile);
        
        // Final validation check before save
        if (profile.getFullName() != null) {
            String trimmed = profile.getFullName().trim();
            if (trimmed.length() < ProfileConstants.FULL_NAME_MIN) {
                log.warn("FullName has length < {} in updateProfile, setting to null. Value: '{}' (length: {})", 
                        ProfileConstants.FULL_NAME_MIN, profile.getFullName(), trimmed.length());
                profile.setFullName(null);
            } else {
                profile.setFullName(trimmed);
            }
        }
        
        Profile savedProfile = profileRepository.save(profile);
        log.debug("Profile updated successfully for userId: {}", currentUserId);
        return profileMapper.toProfileResponse(savedProfile);
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
    @Transactional
    public ProfileResponse getMyProfile() {
        String currentUserId = AuthUtils.getCurrentUserId();
        log.debug("Getting current user's profile: {}", currentUserId);
        
        // Try to get existing profile
        Profile profile = profileRepository.findByUserId(currentUserId)
                .orElseGet(() -> {
                    log.info("Profile not found for current user {}. Creating new profile and syncing from FTES.", currentUserId);
                    return createProfileAndSyncFromFtes(currentUserId);
                });
        
        // Normalize immediately after loading to fix any invalid data from database
        normalizeProfileStrings(profile);
        
        // Sync necessary fields from FTES if profile exists
        // Note: syncProfileFromFtes normalizes and saves the profile if FTES data is available
        syncProfileFromFtes(profile, currentUserId);
        
        // Additional normalize to ensure all fields are normalized after sync
        normalizeProfileStrings(profile);
        
        // Final safety check: ensure fullName is valid before any database operations
        if (profile.getFullName() != null) {
            String trimmed = profile.getFullName().trim();
            if (trimmed.length() < ProfileConstants.FULL_NAME_MIN) {
                log.warn("FullName has length < {} in getMyProfile, setting to null. Value: '{}' (trimmed length: {})", 
                        ProfileConstants.FULL_NAME_MIN, profile.getFullName(), trimmed.length());
                profile.setFullName(null);
            } else if (!profile.getFullName().equals(trimmed)) {
                profile.setFullName(trimmed);
            }
        }
        
        // Save profile if it was normalized (to persist any null conversions from empty strings)
        if (profile.getId() != null) {
            try {
                profileRepository.save(profile);
                profileRepository.flush();
                log.debug("Successfully saved and flushed profile for userId: {}", currentUserId);
            } catch (Exception e) {
                log.error("Failed to save normalized profile for userId: {}. Error: {}", currentUserId, e.getMessage());
                // If save fails due to validation, force set fullName to null and retry
                if (profile.getFullName() != null) {
                    String trimmed = profile.getFullName().trim();
                    if (trimmed.length() < ProfileConstants.FULL_NAME_MIN) {
                        log.warn("Retry: Setting fullName to null due to length < {}. Original value: '{}' (length: {})", 
                                ProfileConstants.FULL_NAME_MIN, profile.getFullName(), trimmed.length());
                        profile.setFullName(null);
                        profileRepository.save(profile);
                        profileRepository.flush();
                        log.info("Successfully saved profile after setting fullName to null");
                    } else {
                        throw e;
                    }
                } else {
                    throw e;
                }
            }
        }
        
        return profileMapper.toProfileResponse(profile);
    }
    
    /**
     * Create new profile and sync data from FTES
     * If FTES sync fails, creates an empty profile with default values from DB
     */
    private Profile createProfileAndSyncFromFtes(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        
        // Create default profile
        UserLevel userLevel = getDefaultUserLevel();
        Profile profile = buildDefaultProfile(user, userLevel);
        
        // Normalize BEFORE first save to ensure no validation errors
        normalizeProfileStrings(profile);
        
        // Save profile first to generate ID
        profile = profileRepository.save(profile);
        
        // Create user stats
        UserStats userStats = createUserStats(profile);
        profile.setUserStats(userStats);
        
        // Try to sync from FTES (this will normalize and save if FTES data is available)
        // If sync fails, we'll still have a profile with default values
        try {
            syncProfileFromFtes(profile, userId);
            log.debug("FTES sync completed for userId: {}", userId);
        } catch (Exception e) {
            log.warn("FTES sync failed for userId: {}. Will use default profile values. Error: {}", 
                    userId, e.getMessage());
        }
        
        // Normalize again after sync (or if sync didn't run) to ensure all fields are normalized
        normalizeProfileStrings(profile);
        
        // Final check: ensure fullName is valid before final save
        if (profile.getFullName() != null) {
            String finalFullName = profile.getFullName().trim();
            if (finalFullName.isEmpty() || finalFullName.length() < ProfileConstants.FULL_NAME_MIN) {
                log.warn("FINAL CHECK in createProfileAndSyncFromFtes: FullName has length < {}, setting to null. Value: '{}' (length: {})", 
                        ProfileConstants.FULL_NAME_MIN, profile.getFullName(), finalFullName.length());
                profile.setFullName(null);
            } else if (!profile.getFullName().equals(finalFullName)) {
                profile.setFullName(finalFullName);
            }
        }
        
        // Save profile again after sync and normalize
        try {
            Profile savedProfile = profileRepository.save(profile);
            log.info("Profile created successfully for userId: {}", userId);
            return savedProfile;
        } catch (Exception e) {
            log.error("Failed to save profile in createProfileAndSyncFromFtes. Profile ID: {}, fullName: '{}' (length: {}), error: {}", 
                    profile.getId(),
                    profile.getFullName(),
                    profile.getFullName() != null ? profile.getFullName().length() : 0,
                    e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Sync profile data from FTES system
     */
    private void syncProfileFromFtes(Profile profile, String userId) {
        try {
            String authToken = getCurrentAuthToken();
            if (authToken == null) {
                log.warn("Cannot get auth token, skipping FTES sync for userId: {}", userId);
                return;
            }
            
            FtesProfileResponse ftesProfile = ftesProfileService.getProfileFromFtes(userId, authToken);
            
            // Sync necessary fields from FTES
            if (ftesProfile != null) {
                // Sync fullName - only set if FTES has a valid name (length >= 2)
                if (ftesProfile.getName() != null) {
                    String trimmedName = ftesProfile.getName().trim();
                    // Only set if trimmed length is >= 2, otherwise set to null
                    if (trimmedName.length() >= ProfileConstants.FULL_NAME_MIN) {
                        profile.setFullName(trimmedName);
                    } else {
                        // If FTES returns empty string or length < 2, set to null
                        profile.setFullName(null);
                        log.debug("FTES returned invalid fullName (length: {}), setting to null for userId: {}", 
                                trimmedName.length(), userId);
                    }
                }
                
                // Sync dateOfBirth
                if (ftesProfile.getDateOfBirth() != null) {
                    Date dob = ftesProfile.getDateOfBirth();
                    profile.setDateOfBirth(dob.toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate());
                }
                
                // Sync major (from jobName in FTES)
                if (ftesProfile.getJobName() != null && !ftesProfile.getJobName().trim().isEmpty()) {
                    profile.setMajor(ftesProfile.getJobName());
                }
                
                // Sync username from Profile API
                if (ftesProfile.getUsername() != null && !ftesProfile.getUsername().trim().isEmpty()) {
                    profile.setUsername(ftesProfile.getUsername());
                } else {
                    // If Profile API doesn't have username, try to get from UserInfo API
                    try {
                        FtesUserInfoResponse userInfo = ftesProfileService.getUserInfoFromFtes(authToken);
                        if (userInfo != null && userInfo.getUsername() != null && !userInfo.getUsername().trim().isEmpty()) {
                            profile.setUsername(userInfo.getUsername());
                        }
                    } catch (Exception e) {
                        log.warn("Failed to get username from FTES UserInfo API for userId: {}. Error: {}", userId, e.getMessage());
                        // Continue without username - it's not critical
                    }
                }
                
                // Sync email if not set
                if (profile.getEmail() == null && ftesProfile.getEmail() != null) {
                    profile.setEmail(ftesProfile.getEmail());
                }
                
                // Sync avatar if not set
                if (profile.getAvtUrl() == null && ftesProfile.getAvatar() != null) {
                    profile.setAvtUrl(ftesProfile.getAvatar());
                }
                
                // Normalize empty strings to null before saving to avoid validation errors
                normalizeProfileStrings(profile);
                
                // Final safety check: ensure fullName is valid before saving
                if (profile.getFullName() != null && profile.getFullName().trim().length() < ProfileConstants.FULL_NAME_MIN) {
                    log.warn("FullName has length < {} in syncProfileFromFtes, setting to null. Value: '{}' (length: {})", 
                            ProfileConstants.FULL_NAME_MIN, profile.getFullName(), profile.getFullName().trim().length());
                    profile.setFullName(null);
                }
                
                // Save updated profile
                profileRepository.save(profile);
                log.debug("Successfully synced profile from FTES for userId: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to sync profile from FTES for userId: {}. Error: {}", userId, e.getMessage());
            // Don't throw exception, just log warning - profile will use default values
        }
    }
    
    /**
     * Get current Authorization token from request
     */
    private String getCurrentAuthToken() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    return authHeader; // Return full "Bearer <token>" string
                }
            }
        } catch (Exception e) {
            log.warn("Failed to get auth token from request: {}", e.getMessage());
        }
        return null;
    }

    private UserLevel getDefaultUserLevel() {
        return userLevelRepository.findByLevel(LevelEnums.COPPER)
                .orElseThrow(() -> {
                    log.error("Cannot find default user level: {}", LevelEnums.COPPER);
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                });
    }

    /**
     * Create default profile if not exists.
     * Extracted to avoid code duplication.
     */
    @Transactional
    private Profile createDefaultProfileIfNotExists(User user) {
        UserLevel userLevel = getDefaultUserLevel();
        Profile profile = buildDefaultProfile(user, userLevel);
        
        // Normalize empty strings to null before saving to avoid validation errors
        normalizeProfileStrings(profile);
        
        // Save profile first to generate ID
        profile = profileRepository.save(profile);
        
        // Create user stats with saved profile
        UserStats userStats = createUserStats(profile);
        
        // Set user stats to profile
        profile.setUserStats(userStats);
        
        return profile;
    }

    private Profile buildDefaultProfile(User user, UserLevel defaultUserLevel) {
        // Set fullName to null - user can set it later when they update their profile
        // This avoids validation errors for new users who haven't filled in their profile yet
        
        return Profile.builder()
                .avtUrl(ProfileConstants.DEFAULT_AVT_URL)
                .fullName(null) // Null is allowed by validation, empty string is not
                .email(null) // Email will be set later by user if needed
                .gender(GenderEnums.OTHER)
                .isVerified(false)
                .isBlocked(false)
                .isActive(true)
                .level(defaultUserLevel)
                .user(user)
                .build();
    }
    
    /**
     * Normalize empty strings to null to avoid validation errors
     * Empty strings will fail @Size(min=2) and @Pattern validation
     */
    private void normalizeProfileStrings(Profile profile) {
        // Normalize fullName: empty string or length < 2 -> null
        // This prevents validation errors since @Size(min=2) requires at least 2 characters
        if (profile.getFullName() != null) {
            String trimmed = profile.getFullName().trim();
            if (trimmed.isEmpty() || trimmed.length() < ProfileConstants.FULL_NAME_MIN) {
                profile.setFullName(null);
            } else {
                profile.setFullName(trimmed);
            }
        }
        
        // Normalize other string fields that might have validation constraints
        if (profile.getBio() != null && profile.getBio().trim().isEmpty()) {
            profile.setBio(null);
        } else if (profile.getBio() != null) {
            profile.setBio(profile.getBio().trim());
        }
        
        if (profile.getAddress() != null && profile.getAddress().trim().isEmpty()) {
            profile.setAddress(null);
        } else if (profile.getAddress() != null) {
            profile.setAddress(profile.getAddress().trim());
        }
        
        if (profile.getUsername() != null && profile.getUsername().trim().isEmpty()) {
            profile.setUsername(null);
        } else if (profile.getUsername() != null) {
            profile.setUsername(profile.getUsername().trim());
        }
        
        if (profile.getMajor() != null && profile.getMajor().trim().isEmpty()) {
            profile.setMajor(null);
        } else if (profile.getMajor() != null) {
            profile.setMajor(profile.getMajor().trim());
        }
    }

    private UserStats createUserStats(Profile profile) {
        CreateUserStatsRequest createUserStatsRequest = CreateUserStatsRequest.builder()
                .profile(profile)
                .build();
        return userStatsService.createUserStats(createUserStatsRequest);
    }

}
