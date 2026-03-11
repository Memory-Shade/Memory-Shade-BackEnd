package com.memoryshade.domain.goal.repository;

import com.memoryshade.domain.goal.exception.GoalErrorCode;
import com.memoryshade.domain.goal.model.Goal;
import com.memoryshade.global.exception.ExceptionList;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface GoalRepository extends Repository<Goal, Long> {

    Goal save(Goal goal);

    Optional<Goal> findByUser_UserId(Long userId);

    default Goal getByUserId(Long userId) {
        return findByUser_UserId(userId).orElseThrow(() -> new ExceptionList(GoalErrorCode.GOAL_NOT_FOUND));
    }
}
