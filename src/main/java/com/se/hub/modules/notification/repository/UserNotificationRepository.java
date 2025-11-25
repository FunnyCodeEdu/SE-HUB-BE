package com.se.hub.modules.notification.repository;

import com.se.hub.modules.notification.entity.UserNotification;
import com.se.hub.modules.notification.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, String>, JpaSpecificationExecutor<UserNotification> {
    
    @Query("SELECT un FROM UserNotification un JOIN FETCH un.notification JOIN un.user u JOIN u.user WHERE u.id = :userId AND un.status = :status ORDER BY un.createDate DESC")
    Page<UserNotification> findAllByUser_IdAndStatusOrderByCreateDateDesc(@Param("userId") String userId, @Param("status") NotificationStatus status, Pageable pageable);
    
    @Query("SELECT un FROM UserNotification un JOIN FETCH un.notification JOIN un.user u JOIN u.user WHERE u.id = :userId ORDER BY un.createDate DESC")
    Page<UserNotification> findAllByUser_IdOrderByCreateDateDesc(@Param("userId") String userId, Pageable pageable);
    
    @Query("SELECT COUNT(un) FROM UserNotification un JOIN un.user u JOIN u.user WHERE u.id = :userId AND un.status = :status")
    long countByUser_IdAndStatus(@Param("userId") String userId, @Param("status") NotificationStatus status);
    
    @Query("SELECT un FROM UserNotification un JOIN FETCH un.notification JOIN FETCH un.user u JOIN FETCH u.user WHERE un.id = :id AND u.id = :userId")
    Optional<UserNotification> findByIdAndUser_Id(@Param("id") String id, @Param("userId") String userId);
    
    @Modifying
    @Query("UPDATE UserNotification un SET un.status = :status, un.readAt = :readAt WHERE EXISTS (SELECT 1 FROM Profile p JOIN p.user u WHERE p.id = un.user.id AND u.id = :userId) AND un.status = :oldStatus")
    int markAllAsReadByUserId(@Param("userId") String userId, 
                               @Param("status") NotificationStatus status,
                               @Param("oldStatus") NotificationStatus oldStatus,
                               @Param("readAt") Instant readAt);
    
    @Modifying
    @Query("UPDATE UserNotification un SET un.status = :status, un.readAt = :readAt WHERE un.id = :id AND EXISTS (SELECT 1 FROM Profile p JOIN p.user u WHERE p.id = un.user.id AND u.id = :userId)")
    int markAsReadByIdAndUserId(@Param("id") String id,
                                 @Param("userId") String userId,
                                 @Param("status") NotificationStatus status,
                                 @Param("readAt") Instant readAt);
    
    @Modifying
    @Query("UPDATE UserNotification un SET un.status = :status WHERE EXISTS (SELECT 1 FROM Profile p JOIN p.user u WHERE p.id = un.user.id AND u.id = :userId) AND un.status = :oldStatus")
    int updateStatusByUserId(@Param("userId") String userId,
                             @Param("status") NotificationStatus status,
                             @Param("oldStatus") NotificationStatus oldStatus);
    
    @Query("SELECT un FROM UserNotification un JOIN FETCH un.notification JOIN un.user u JOIN u.user WHERE u.id = :userId AND un.status = :status ORDER BY un.createDate DESC")
    List<UserNotification> findTopNByUser_IdAndStatusOrderByCreateDateDesc(@Param("userId") String userId, @Param("status") NotificationStatus status, Pageable pageable);
    
    @Query("SELECT COUNT(un) FROM UserNotification un JOIN un.user u JOIN u.user WHERE u.id = :userId AND un.status = :status")
    long countUnreadByUserIdAndStatus(@Param("userId") String userId, @Param("status") NotificationStatus status);
}


