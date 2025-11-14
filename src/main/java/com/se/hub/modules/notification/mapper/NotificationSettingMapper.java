package com.se.hub.modules.notification.mapper;

import com.se.hub.modules.notification.dto.request.UpdateNotificationSettingRequest;
import com.se.hub.modules.notification.dto.response.NotificationSettingResponse;
import com.se.hub.modules.notification.entity.NotificationSetting;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NotificationSettingMapper {
    NotificationSettingResponse toNotificationSettingResponse(NotificationSetting setting);
    
    @org.mapstruct.Mapping(target = "id", ignore = true)
    @org.mapstruct.Mapping(target = "createDate", ignore = true)
    @org.mapstruct.Mapping(target = "createdBy", ignore = true)
    @org.mapstruct.Mapping(target = "updatedDate", ignore = true)
    @org.mapstruct.Mapping(target = "updateBy", ignore = true)
    @org.mapstruct.Mapping(target = "user", ignore = true)
    NotificationSetting updateSettingFromRequest(@MappingTarget NotificationSetting setting, UpdateNotificationSettingRequest request);
}

