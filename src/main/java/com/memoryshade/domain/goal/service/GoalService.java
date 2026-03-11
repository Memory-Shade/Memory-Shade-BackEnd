package com.memoryshade.domain.goal.service;

import com.memoryshade.domain.goal.dto.GoalCreateRequestDto;
import com.memoryshade.domain.goal.dto.GoalCreateResponseDto;
import com.memoryshade.domain.goal.dto.GoalGetResponseDto;
import com.memoryshade.domain.goal.dto.GoalProgressResponseDto;
import com.memoryshade.domain.goal.model.Goal;
import com.memoryshade.domain.goal.repository.GoalRecordRepository;
import com.memoryshade.domain.goal.repository.GoalRepository;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalRecordRepository goalRecordRepository;

    @Transactional
    public GoalCreateResponseDto create(Long loginUserId, GoalCreateRequestDto request) {

        User user = userRepository.getByUserId(loginUserId);

        Goal goal = goalRepository.save(
                Goal.builder()
                .user(user)
                .title(request.title())
                .build());

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

        long achievedCount = goalRecordRepository.
                countAchievedByGoalIdAndCreatedAtBetween(goal.getGoalId(), start, end);

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double progress = days == 0 ? 0.0 : (double) achievedCount / days;

        return new GoalProgressResponseDto(startDate, endDate, progress);
    }

}
