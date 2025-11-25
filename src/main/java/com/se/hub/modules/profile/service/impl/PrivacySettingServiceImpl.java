package com.se.hub.modules.profile.service.impl;

import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.profile.dto.request.UpdatePrivacySettingRequest;
import com.se.hub.modules.profile.dto.response.PrivacySettingResponse;
import com.se.hub.modules.profile.entity.PrivacySetting;
import com.se.hub.modules.profile.mapper.PrivacySettingMapper;
import com.se.hub.modules.profile.repository.PrivacySettingRepository;
import com.se.hub.modules.profile.repository.ProfileRepository;
import com.se.hub.modules.profile.service.api.PrivacySettingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivacySettingServiceImpl implements PrivacySettingService {
    PrivacySettingRepository privacySettingRepository;
    PrivacySettingMapper privacySettingMapper;
    ProfileRepository profileRepository;

    @Override
    public PrivacySettingResponse getPrivacySettings() {
        log.debug("PrivacySettingService_getPrivacySettings_Getting privacy settings for user: {}", AuthUtils.getCurrentUserId());
        String userId = AuthUtils.getCurrentUserId();
        
        PrivacySetting setting = privacySettingRepository.findByUser_User_Id(userId)
                .orElseGet(() -> {
                    // Create default settings if not exists
                    PrivacySetting defaultSetting = PrivacySetting.builder()
                            .user(profileRepository.findByUserId(userId)
                                    .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND)))
                            .build();
                    defaultSetting.setCreatedBy(userId);
                    defaultSetting.setUpdateBy(userId);
                    return privacySettingRepository.save(defaultSetting);
                });
        
        return privacySettingMapper.toPrivacySettingResponse(setting);
    }

    @Override
    @Transactional
    public PrivacySettingResponse updatePrivacySettings(UpdatePrivacySettingRequest request) {
        log.debug("PrivacySettingService_updatePrivacySettings_Updating privacy settings for user: {}", AuthUtils.getCurrentUserId());
        String userId = AuthUtils.getCurrentUserId();
        
        PrivacySetting setting = privacySettingRepository.findByUser_User_Id(userId)
                .orElseGet(() -> {
                    PrivacySetting newSetting = PrivacySetting.builder()
                            .user(profileRepository.findByUserId(userId)
                                    .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND)))
                            .build();
                    newSetting.setCreatedBy(userId);
                    newSetting.setUpdateBy(userId);
                    return privacySettingRepository.save(newSetting);
                });
        
        setting = privacySettingMapper.updateSettingFromRequest(setting, request);
        setting.setUpdateBy(userId);
        
        PrivacySetting savedSetting = privacySettingRepository.save(setting);
        log.debug("PrivacySettingService_updatePrivacySettings_Privacy settings updated successfully");
        
        return privacySettingMapper.toPrivacySettingResponse(savedSetting);
    }
}

