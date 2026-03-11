package com.memoryshade.domain.notification.service;

import com.memoryshade.domain.notification.dto.NotificationCreateRequestDto;
import com.memoryshade.domain.notification.dto.NotificationResponseDto;
import com.memoryshade.domain.notification.dto.NotificationUpdateReadRequestDto;
import com.memoryshade.domain.notification.model.Notification;
import com.memoryshade.domain.notification.repository.NotificationRepository;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationResponseDto create(Long loginUserId, NotificationCreateRequestDto request) {

        User user = userRepository.getByUserId(loginUserId);
        Notification notification = notificationRepository.save(request.toNotification(user));
        return NotificationResponseDto.fromNotification(notification);
    }

    public List<NotificationResponseDto> getMyNotifications(Long loginUserId) {
        return notificationRepository.
                findAllByUser_UserIdOrderByCreatedAtDesc(loginUserId)
                .stream()
                .map(NotificationResponseDto::fromNotification)
                .toList();
    }

    public NotificationResponseDto updateRead(
            Long loginUserId,
            Long notiId,
            NotificationUpdateReadRequestDto request) {
        Notification notification = notificationRepository.getByNotiIdAndUserId(loginUserId, notiId);
        notification.updateAsRead();
        return NotificationResponseDto.fromNotification(notification);
    }
}
