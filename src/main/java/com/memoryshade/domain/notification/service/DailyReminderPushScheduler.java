package com.memoryshade.domain.notification.service;

import com.memoryshade.domain.user.model.Role;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DailyReminderPushScheduler {

    private final UserRepository userRepository;
    private final ExpoPushService expoPushService;

    @Scheduled(cron = "0 0 15,17,20 * * *", zone = "Asia/Seoul")
    public void sendDailyReminderPush() {
        List<User> users = userRepository.findAllByRoleAndFcmTokenIsNotNull(Role.USER);

        for (User user : users) {
            expoPushService.sendDailyReminder(user.getFcmToken());
        }
    }

    @Scheduled(cron = "0 0 14,16,18 * * *", zone = "Asia/Seoul")
    public void sendConditionReminderPush() {
        List<User> users = userRepository.findAllByRoleAndFcmTokenIsNotNull(Role.USER);

        for (User user : users) {
            expoPushService.sendGameReminder(user.getFcmToken());
        }
    }
}
