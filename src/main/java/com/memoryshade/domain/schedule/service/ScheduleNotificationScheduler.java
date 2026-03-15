package com.memoryshade.domain.schedule.service;

import com.memoryshade.domain.notification.dto.NotificationCreateRequestDto;
import com.memoryshade.domain.notification.model.NotiType;
import com.memoryshade.domain.notification.service.NotificationService;
import com.memoryshade.domain.schedule.model.Schedule;
import com.memoryshade.domain.schedule.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduleNotificationScheduler {

    private final ScheduleRepository scheduleRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void notifySchedules() {
        List<Schedule> schedules = scheduleRepository.findSchedulesToNotify(LocalDateTime.now());

        for (Schedule schedule : schedules) {
            notificationService.create(
                    schedule.getUser().getUserId(),
                    new NotificationCreateRequestDto(
                            schedule.getTitle(), //TODO: "일정이 생성되었습니다. title + alarmtime
                            NotiType.SCHEDULE
                    )
            );
            schedule.markNotified();
        }
    }
}
