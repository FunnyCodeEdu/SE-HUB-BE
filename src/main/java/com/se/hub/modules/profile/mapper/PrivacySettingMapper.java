package com.se.hub.modules.profile.mapper;

import com.se.hub.modules.profile.dto.request.UpdatePrivacySettingRequest;
import com.se.hub.modules.profile.dto.response.PrivacySettingResponse;
import com.se.hub.modules.profile.entity.PrivacySetting;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PrivacySettingMapper {
    PrivacySettingResponse toPrivacySettingResponse(PrivacySetting setting);
    
    @org.mapstruct.Mapping(target = "id", ignore = true)
    @org.mapstruct.Mapping(target = "createDate", ignore = true)
    @org.mapstruct.Mapping(target = "createdBy", ignore = true)
    @org.mapstruct.Mapping(target = "updatedDate", ignore = true)
    @org.mapstruct.Mapping(target = "updateBy", ignore = true)
    @org.mapstruct.Mapping(target = "user", ignore = true)
    PrivacySetting updateSettingFromRequest(@MappingTarget PrivacySetting setting, UpdatePrivacySettingRequest request);
}

