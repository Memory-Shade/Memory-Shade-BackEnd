package com.memoryshade.domain.notification.repository;

import com.memoryshade.domain.notification.exception.NotificationErrorCode;
import com.memoryshade.domain.notification.model.Notification;
import com.memoryshade.global.exception.ExceptionList;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends Repository<Notification, Long> {

    Notification save(Notification notification);

    Optional<Notification> findByNotiIdAndUser_UserId(Long notiId, Long userId);

    List<Notification> findAllByUser_UserIdOrderByCreatedAtDesc(Long userId);

    default Notification getByNotiIdAndUserId(Long notiId, Long userId) {
        return findByNotiIdAndUser_UserId(notiId, userId)
                .orElseThrow(() -> new ExceptionList(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
    }
}
