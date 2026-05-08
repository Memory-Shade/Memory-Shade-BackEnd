package com.memoryshade.domain.step.repository;

import com.memoryshade.domain.step.model.DailyStepRecord;
import org.springframework.data.repository.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyStepRecordRepository extends Repository<DailyStepRecord, Long> {

    DailyStepRecord save(DailyStepRecord dailyStepRecord);

    Optional<DailyStepRecord> findByUser_UserIdAndRecordDate(Long userId, LocalDate recordDate);

    List<DailyStepRecord> findAllByUser_UserIdAndRecordDateBetweenOrderByRecordDateAsc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );
}
