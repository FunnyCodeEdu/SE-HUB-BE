package com.se.hub.modules.notification.mapper;

import com.se.hub.modules.notification.dto.response.NotificationResponse;
import com.se.hub.modules.notification.entity.UserNotification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(source = "notification.notificationType", target = "notificationType")
    @Mapping(source = "notification.title", target = "title")
    @Mapping(source = "notification.content", target = "content")
    @Mapping(source = "notification.metadata", target = "metadata")
    @Mapping(source = "notification.targetType", target = "targetType")
    @Mapping(source = "notification.targetId", target = "targetId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "readAt", target = "readAt")
    NotificationResponse toNotificationResponse(UserNotification userNotification);

    /**
     * Map list of UserNotification entities to list of NotificationResponse DTOs
     * @param userNotifications list of UserNotification entities
     * @return list of NotificationResponse DTOs
     */
    List<NotificationResponse> toListNotificationResponse(List<UserNotification> userNotifications);
}


