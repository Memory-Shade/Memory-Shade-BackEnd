package com.memoryshade.domain.notification.service;

import com.memoryshade.domain.guardianLink.model.GuardianLink;
import com.memoryshade.domain.guardianLink.repository.GuardianLinkRepository;
import com.memoryshade.domain.notification.dto.NotificationCreateRequestDto;
import com.memoryshade.domain.notification.dto.NotificationResponseDto;
import com.memoryshade.domain.notification.dto.NotificationUpdateReadRequestDto;
import com.memoryshade.domain.notification.model.NotiType;
import com.memoryshade.domain.notification.model.Notification;
import com.memoryshade.domain.notification.repository.NotificationRepository;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final GuardianLinkRepository guardianLinkRepository;

    public NotificationResponseDto create(Long loginUserId, NotificationCreateRequestDto request) {

        User user = userRepository.getByUserId(loginUserId);
        Notification notification = notificationRepository.save(request.toNotification(user));
        return NotificationResponseDto.fromNotification(notification);
    }

    public List<NotificationResponseDto> getMyNotifications(Long loginUserId, NotiType type, Boolean isRead) {
        return notificationRepository.findByUserIdAndFilters(loginUserId, type, isRead)
                .stream()
                .map(NotificationResponseDto::fromNotification)
                .toList();
    }

    public NotificationResponseDto updateRead(
            Long loginUserId,
            Long notiId,
            NotificationUpdateReadRequestDto request) {
        Notification notification = notificationRepository.getByNotiIdAndUserId(notiId, loginUserId);
        notification.updateAsRead();
        return NotificationResponseDto.fromNotification(notification);
    }

    public void createDiarySharedNotifications(Long userId, String userName, LocalDate diaryDate) {
        List<GuardianLink> guardianLinks = guardianLinkRepository.findAllByUser_UserId(userId);
        String content = userName + "님의 " + diaryDate + " 일기가 공유되었습니다."; //TODO: builder로 수정

        guardianLinks.stream()
                .map(GuardianLink::getGuardian)
                .map(guardian -> Notification.builder()
                        .user(guardian)
                        .content(content)
                        .notiType(NotiType.DIARY_SHARED)
                        .build())
                .forEach(notificationRepository::save);
    }
}
