package com.memoryshade.domain.goal.service;

import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.diary.repository.DiaryRepository;
import com.memoryshade.domain.goal.dto.GoalAchievementRequestDto;
import com.memoryshade.domain.goal.dto.GoalAchievementResponseDto;
import com.memoryshade.domain.goal.dto.GoalCreateRequestDto;
import com.memoryshade.domain.goal.dto.GoalCreateResponseDto;
import com.memoryshade.domain.goal.dto.GoalGetResponseDto;
import com.memoryshade.domain.goal.dto.GoalProgressResponseDto;
import com.memoryshade.domain.goal.exception.GoalErrorCode;
import com.memoryshade.domain.goal.model.Goal;
import com.memoryshade.domain.goal.model.GoalRecord;
import com.memoryshade.domain.goal.repository.GoalRecordRepository;
import com.memoryshade.domain.goal.repository.GoalRepository;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import com.memoryshade.global.exception.ExceptionList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalRecordRepository goalRecordRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    public GoalCreateResponseDto create(Long loginUserId, GoalCreateRequestDto request) {
        User user = userRepository.getByUserId(loginUserId);

        Goal goal = goalRepository.save(request.toGoal(user));

        return GoalCreateResponseDto.fromGoal(goal);
    }

    public GoalGetResponseDto getMeGoal(Long loginUserId) {
        return GoalGetResponseDto.fromGoal(goalRepository.getByUserId(loginUserId));
    }

    public GoalProgressResponseDto getProgress(Long loginUserId) {
        Goal goal = goalRepository.getByUserId(loginUserId);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.withDayOfMonth(1);

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = LocalDateTime.now();

        long achievedCount = goalRecordRepository
            .countAchievedByGoalIdAndCreatedAtBetween(goal.getGoalId(), start, end);

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double progress = days == 0 ? 0.0 : (double) achievedCount / days;

        return new GoalProgressResponseDto(startDate, endDate, progress);
    }

    @Transactional
    public GoalAchievementResponseDto checkTodayAchievement(
        Long loginUserId,
        GoalAchievementRequestDto request
    ) {
        Goal goal = goalRepository.getByUserId(loginUserId);

        Diary todayDiary = diaryRepository.findByUser_UserIdAndDiaryDate(
            loginUserId,
            LocalDate.now()
        ).orElseThrow(() -> new ExceptionList(GoalErrorCode.TODAY_DIARY_NOT_FOUND));

        goalRecordRepository.findByGoal_GoalIdAndDiary_DiaryId(
            goal.getGoalId(),
            todayDiary.getDiaryId()
        ).ifPresent(goalRecord -> {
            throw new ExceptionList(GoalErrorCode.GOAL_RECORD_ALREADY_EXISTS);
        });

        GoalRecord goalRecord = goalRecordRepository.save(
            GoalRecord.builder()
                .goal(goal)
                .diary(todayDiary)
                .isAchieved(request.isAchieved())
                .build()
        );

        return GoalAchievementResponseDto.from(goalRecord);
    }
}