package com.se.hub.modules.notification.controller;

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
import com.se.hub.modules.notification.constant.NotificationMessageConstants;
import com.se.hub.modules.notification.dto.request.UpdateNotificationSettingRequest;
import com.se.hub.modules.notification.dto.response.NotificationResponse;
import com.se.hub.modules.notification.dto.response.NotificationSettingResponse;
import com.se.hub.modules.notification.dto.response.UnreadCountResponse;
import com.se.hub.modules.notification.service.api.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Notification Management",
        description = "Notification management API")
@RequestMapping("/notifications")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class NotificationController extends BaseController {
    NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get notifications",
            description = "Get list of notifications for current user with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = NotificationMessageConstants.API_NOTIFICATION_RETRIEVED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = "Bad request"),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<PagingResponse<NotificationResponse>>> getNotifications(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
    ) {
        log.debug("NotificationController_getNotifications_Fetching notifications with page: {}, size: {}", page, size);
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        return success(notificationService.getNotifications(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/{notificationId}")
    @Operation(summary = "Get notification by ID",
            description = "Get notification information by notification ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = NotificationMessageConstants.API_NOTIFICATION_RETRIEVED_BY_ID_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = NotificationMessageConstants.USER_NOTIFICATION_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<NotificationResponse>> getNotificationById(@PathVariable String notificationId) {
        log.debug("NotificationController_getNotificationById_Fetching notification with id: {}", notificationId);
        return success(notificationService.getNotificationById(notificationId), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Mark notification as read",
            description = "Mark a notification as read for current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = NotificationMessageConstants.API_NOTIFICATION_MARKED_READ_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = NotificationMessageConstants.USER_NOTIFICATION_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<Void>> markAsRead(@PathVariable String notificationId) {
        log.debug("NotificationController_markAsRead_Marking notification as read: {}", notificationId);
        notificationService.markAsRead(notificationId);
        return success(null, MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read",
            description = "Mark all notifications as read for current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = NotificationMessageConstants.API_NOTIFICATION_MARKED_ALL_READ_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<Void>> markAllAsRead() {
        log.debug("NotificationController_markAllAsRead_Marking all notifications as read");
        notificationService.markAllAsRead();
        return success(null, MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread count",
            description = "Get unread notification count for current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = NotificationMessageConstants.API_UNREAD_COUNT_RETRIEVED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<UnreadCountResponse>> getUnreadCount() {
        log.debug("NotificationController_getUnreadCount_Getting unread count");
        return success(notificationService.getUnreadCount(), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete notification",
            description = "Delete a notification for current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = NotificationMessageConstants.API_NOTIFICATION_DELETED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = NotificationMessageConstants.USER_NOTIFICATION_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<Void>> deleteNotification(@PathVariable String notificationId) {
        log.debug("NotificationController_deleteNotification_Deleting notification: {}", notificationId);
        notificationService.deleteNotification(notificationId);
        return success(null, MessageCodeConstant.M004_DELETED, MessageConstant.DELETED);
    }

    @GetMapping("/settings")
    @Operation(summary = "Get notification settings",
            description = "Get notification settings for current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = NotificationMessageConstants.API_SETTINGS_RETRIEVED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<NotificationSettingResponse>> getSettings() {
        log.debug("NotificationController_getSettings_Getting notification settings");
        return success(notificationService.getSettings(), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @PutMapping("/settings")
    @Operation(summary = "Update notification settings",
            description = "Update notification settings for current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = NotificationMessageConstants.API_SETTINGS_UPDATED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = "Bad request"),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = "Internal server error")
    })
    public ResponseEntity<GenericResponse<NotificationSettingResponse>> updateSettings(
            @Valid @RequestBody UpdateNotificationSettingRequest request) {
        log.debug("NotificationController_updateSettings_Updating notification settings");
        return success(notificationService.updateSettings(request), MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }
}

