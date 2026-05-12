package com.memoryshade.domain.step.service;

import com.memoryshade.domain.step.dto.DailyStepResponseDto;
import com.memoryshade.domain.step.dto.DailyStepCountResponseDto;
import com.memoryshade.domain.step.dto.DailyStepSaveRequestDto;
import com.memoryshade.domain.step.dto.WeeklyStepStatsResponseDto;
import com.memoryshade.domain.step.exception.StepErrorCode;
import com.memoryshade.domain.step.model.DailyStepRecord;
import com.memoryshade.domain.step.repository.DailyStepRecordRepository;
import com.memoryshade.domain.guardianLink.repository.GuardianLinkRepository;
import com.memoryshade.domain.user.model.Role;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import com.memoryshade.global.exception.ExceptionList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StepService {

    private final DailyStepRecordRepository dailyStepRecordRepository;
    private final UserRepository userRepository;
    private final GuardianLinkRepository guardianLinkRepository;

    @Transactional
    public DailyStepResponseDto saveDailySteps(Long loginUserId, DailyStepSaveRequestDto request) {
        validateAuthenticated(loginUserId);

        User user = userRepository.getByUserId(loginUserId);

        DailyStepRecord record = dailyStepRecordRepository
                .findByUser_UserIdAndRecordDate(loginUserId, request.recordDate())
                .map(existingRecord -> {
                    existingRecord.updateStepCount(request.stepCount());
                    return existingRecord;
                })
                .orElseGet(() -> dailyStepRecordRepository.save(
                        DailyStepRecord.builder()
                                .user(user)
                                .recordDate(request.recordDate())
                                .stepCount(request.stepCount())
                                .build()
                ));

        return DailyStepResponseDto.from(record);
    }

    public WeeklyStepStatsResponseDto getUserWeeklyStats(Long loginUserId, Long userId, LocalDate baseDate) {
        validateAuthenticated(loginUserId);
        validateGuardianCanAccessUser(loginUserId, userId);

        LocalDate targetDate = baseDate == null ? LocalDate.now() : baseDate;
        LocalDate startDate = targetDate.with(DayOfWeek.MONDAY);
        LocalDate endDate = targetDate.with(DayOfWeek.SUNDAY);

        int totalSteps = dailyStepRecordRepository
                .findAllByUser_UserIdAndRecordDateBetweenOrderByRecordDateAsc(
                        userId,
                        startDate,
                        endDate
                )
                .stream()
                .mapToInt(DailyStepRecord::getStepCount)
                .sum();

        int averageSteps = totalSteps / 7;

        return new WeeklyStepStatsResponseDto(averageSteps);
    }

    public DailyStepCountResponseDto getUserDailySteps(Long loginUserId, Long userId, LocalDate recordDate) {
        validateAuthenticated(loginUserId);
        validateGuardianCanAccessUser(loginUserId, userId);

        int stepCount = dailyStepRecordRepository
                .findByUser_UserIdAndRecordDate(userId, recordDate)
                .map(DailyStepRecord::getStepCount)
                .orElse(0);

        return new DailyStepCountResponseDto(stepCount);
    }

    private void validateAuthenticated(Long loginUserId) {
        if (loginUserId == null) {
            throw new ExceptionList(StepErrorCode.UNAUTHORIZED_USER);
        }
    }

    private void validateGuardianCanAccessUser(Long guardianId, Long userId) {
        User guardian = userRepository.getByUserId(guardianId);
        if (guardian.getRole() != Role.GUARDIAN) {
            throw new ExceptionList(StepErrorCode.GUARDIAN_ONLY);
        }

        User user = userRepository.getByUserId(userId);
        if (user.getRole() != Role.USER) {
            throw new ExceptionList(StepErrorCode.TARGET_USER_ONLY);
        }

        guardianLinkRepository.validateLinked(userId, guardianId);
    }
}
