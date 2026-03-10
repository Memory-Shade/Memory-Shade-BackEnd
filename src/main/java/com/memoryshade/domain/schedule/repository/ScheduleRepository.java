package com.memoryshade.domain.schedule.repository;

import com.memoryshade.domain.schedule.exception.ScheduleErrorCode;
import com.memoryshade.domain.schedule.model.Schedule;
import com.memoryshade.global.exception.ExceptionList;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ScheduleRepository extends Repository<Schedule, Long> {

    Schedule save(Schedule schedule);
    void delete(Schedule schedule);

    Optional<Schedule> findByScheduleIdAndUser_UserId(Long scheduleId, Long userId);

    default Schedule getByScheduleIdAndUserId(Long scheduleId, Long userId) {
        return findByScheduleIdAndUser_UserId(scheduleId, userId)
                .orElseThrow(() -> new ExceptionList(ScheduleErrorCode.SCHEDULE_NOT_FOUND));
    }
}
