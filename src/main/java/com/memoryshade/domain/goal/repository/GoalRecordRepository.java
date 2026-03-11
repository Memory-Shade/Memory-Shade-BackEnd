package com.memoryshade.domain.goal.repository;

import com.memoryshade.domain.goal.model.GoalRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;


public interface GoalRecordRepository extends Repository<GoalRecord, Long> {
    @Query("""
    select count(gr)
    from GoalRecord gr
    where gr.goal.goalId = :goalId
      and gr.isAchieved = true
      and gr.createdAt between :start and :end
""")
    long countAchievedByGoalIdAndCreatedAtBetween(
            @Param("goalId") Long goalId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
