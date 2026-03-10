package com.memoryshade.domain.schedule.service;

import com.memoryshade.domain.schedule.dto.ScheduleRequestDto;
import com.memoryshade.domain.schedule.dto.ScheduleResponseDto;
import com.memoryshade.domain.schedule.model.Schedule;
import com.memoryshade.domain.schedule.repository.ScheduleRepository;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
