package com.memoryshade.domain.notification.repository;

import com.memoryshade.domain.notification.exception.NotificationErrorCode;
import com.memoryshade.domain.notification.model.NotiType;
import com.memoryshade.domain.notification.model.Notification;
import com.memoryshade.global.exception.ExceptionList;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends Repository<Notification, Long> {

    Notification save(Notification notification);

    Optional<Notification> findByNotiIdAndUser_UserId(Long notiId, Long userId);

    List<Notification> findAllByUser_UserIdOrderByCreatedAtDesc(Long userId);


    List<Notification> findAllByUser_UserIdAndNotiTypeOrderByCreatedAtDesc(Long userId, NotiType notiType);

    List<Notification> findAllByUser_UserIdAndIsReadOrderByCreatedAtDesc(Long userId, boolean isRead);

    List<Notification> findAllByUser_UserIdAndNotiTypeAndIsReadOrderByCreatedAtDesc(
            Long userId,
            NotiType notiType,
            boolean isRead
    );

    default List<Notification> findByUserIdAndFilters(Long userId, NotiType type, Boolean isRead) {
        if (type != null && isRead != null) {
            return findAllByUser_UserIdAndNotiTypeAndIsReadOrderByCreatedAtDesc(userId, type, isRead);
        }
        if (type != null) {
            return findAllByUser_UserIdAndNotiTypeOrderByCreatedAtDesc(userId, type);
        }
        if (isRead != null) {
            return findAllByUser_UserIdAndIsReadOrderByCreatedAtDesc(userId, isRead);
        }
        return findAllByUser_UserIdOrderByCreatedAtDesc(userId);
    }

    default Notification getByNotiIdAndUserId(Long notiId, Long userId) {
        return findByNotiIdAndUser_UserId(notiId, userId)
                .orElseThrow(() -> new ExceptionList(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
    }
}
