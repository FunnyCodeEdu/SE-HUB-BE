package com.se.hub.modules.profile.service.impl;

import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.profile.constant.userstats.UserStatsConstants;
import com.se.hub.modules.profile.dto.request.CreateUserStatsRequest;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.profile.entity.UserStats;
import com.se.hub.modules.profile.repository.ProfileRepository;
import com.se.hub.modules.profile.repository.UserStatsRepository;
import com.se.hub.modules.profile.service.api.UserStatsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserStatsServiceImpl implements UserStatsService {
    UserStatsRepository userStatsRepository;
    ProfileRepository profileRepository;

    @Override
    public UserStats createUserStats(CreateUserStatsRequest request) {
        Profile profile = request.getProfile();

        //validate profile existed
        boolean existed = userStatsRepository.existsByProfileId(profile.getId());
        if(existed){
            log.error("Profile with id {} already exists Stats", profile.getId());
            throw new AppException(ErrorCode.DATA_EXISTED);
        }

        //build user stats
        UserStats userStats = UserStats.builder()
                .points(UserStatsConstants.DEFAULT_POINTS)
                .examsDone(UserStatsConstants.DEFAULT_EXAMS_DONE)
                .cmtCount(UserStatsConstants.DEFAULT_COMMENT_COUNT)
                .docsUploaded(UserStatsConstants.DEFAULT_DOCS_UPLOADED)
                .blogsUploaded(UserStatsConstants.DEFAULT_POSTS_UPLOADED)
                .blogsShared(UserStatsConstants.DEFAULT_POSTS_SHARED)
                .profile(profile)
                .build();

        //save user stats
        return userStatsRepository.save(userStats);
    }

    @Override
    public UserStats getUserStatsByProfileId(String profileId) {
        // check for profile existed
        if (!profileRepository.existsById(profileId)) {
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }
        return userStatsRepository.findByProfileId(profileId)
                .orElseThrow(() -> {
                    log.error("Profile with id {} not found", profileId);
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                });
    }

    @Override
    public UserStats getUserStatsById(String id) {
        return userStatsRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User stats with id {} not found", id);
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                });
    }

    @Override
    public UserStats resetUserStatsByProfileId(String profileId) {
        // check for profile existed
        if (!profileRepository.existsById(profileId)) {
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }

        // get existing user stats
        UserStats existingUserStats = userStatsRepository.findByProfileId(profileId)
                .orElseThrow(() -> {
                    log.error("User stats with profile id {} not found", profileId);
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                });

        // reset user stats to default values
        buildDefaultUserStats(existingUserStats);

        return userStatsRepository.save(existingUserStats);
    }

    private void buildDefaultUserStats(UserStats userStats) {
        userStats.setPoints(UserStatsConstants.DEFAULT_POINTS);
        userStats.setExamsDone(UserStatsConstants.DEFAULT_EXAMS_DONE);
        userStats.setCmtCount(UserStatsConstants.DEFAULT_COMMENT_COUNT);
        userStats.setDocsUploaded(UserStatsConstants.DEFAULT_DOCS_UPLOADED);
        userStats.setBlogsUploaded(UserStatsConstants.DEFAULT_POSTS_UPLOADED);
        userStats.setBlogsShared(UserStatsConstants.DEFAULT_POSTS_SHARED);
    }
}
