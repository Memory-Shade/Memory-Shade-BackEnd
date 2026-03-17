package com.memoryshade.domain.schedule.service;

import com.memoryshade.domain.notification.dto.NotificationCreateRequestDto;
import com.memoryshade.domain.notification.model.NotiType;
import com.memoryshade.domain.notification.service.NotificationService;
import com.memoryshade.domain.schedule.dto.ScheduleRequestDto;
import com.memoryshade.domain.schedule.dto.ScheduleResponseDto;
import com.memoryshade.domain.schedule.model.Schedule;
import com.memoryshade.domain.schedule.repository.ScheduleRepository;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public ScheduleResponseDto create(Long loginUserId, ScheduleRequestDto request) {
        User user = userRepository.getByUserId(loginUserId);

        Schedule schedule = request.toSchedule(user);
        scheduleRepository.save(schedule);

        return ScheduleResponseDto.fromSchedule(schedule);
    }

    public List<ScheduleResponseDto> getAllSchedulesByDate(Long loginUserId, LocalDate date) {
        userRepository.getByUserId(loginUserId);

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return scheduleRepository.findAllSchedulesByUserIdAndDate(loginUserId, start, end)
                .stream()
                .map(ScheduleResponseDto::fromSchedule)
                .toList();
    }

    public ScheduleResponseDto update(Long loginUserId, Long scheduleId, ScheduleRequestDto request) {
        Schedule schedule = scheduleRepository.getByScheduleIdAndUserId(scheduleId, loginUserId);
        schedule.updateSchedule(request.title(), request.alarmTime());
        return ScheduleResponseDto.fromSchedule(schedule);
    }

    public void delete(Long loginUserId, Long scheduleId) {
        Schedule schedule = scheduleRepository.getByScheduleIdAndUserId(scheduleId, loginUserId);
        scheduleRepository.delete(schedule);
    }
}
