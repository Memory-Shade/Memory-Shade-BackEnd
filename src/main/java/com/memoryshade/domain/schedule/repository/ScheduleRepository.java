package com.memoryshade.domain.schedule.repository;

import com.memoryshade.domain.schedule.exception.ScheduleErrorCode;
import com.memoryshade.domain.schedule.model.Schedule;
import com.memoryshade.global.exception.ExceptionList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends Repository<Schedule, Long> {

    Schedule save(Schedule schedule);
    void delete(Schedule schedule);

    Optional<Schedule> findByScheduleIdAndUser_UserId(Long scheduleId, Long userId);

    default Schedule getByScheduleIdAndUserId(Long scheduleId, Long userId) {
        return findByScheduleIdAndUser_UserId(scheduleId, userId)
                .orElseThrow(() -> new ExceptionList(ScheduleErrorCode.SCHEDULE_NOT_FOUND));
    }

    @Query("""
        select s
        from Schedule s
        where s.user.userId = :userId
          and s.alarmTime >= :start
          and s.alarmTime < :end
        order by s.alarmTime asc
        """)
    List<Schedule> findAllSchedulesByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        select s
        from Schedule s
        where s.alarmTime <= :now
          and s.notifiedAt is null
        order by s.alarmTime asc
        """)
    List<Schedule> findSchedulesToNotify(@Param("now") LocalDateTime now);
}
