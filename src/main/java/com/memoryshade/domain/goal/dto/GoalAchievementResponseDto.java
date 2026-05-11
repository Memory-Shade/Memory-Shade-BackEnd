package com.memoryshade.domain.goal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.memoryshade.domain.goal.model.GoalRecord;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoalAchievementResponseDto(
    @JsonProperty("record_id")
    Long recordId,

    @JsonProperty("goal_id")
    Long goalId,

    @JsonProperty("diary_id")
    Long diaryId,

    @JsonProperty("is_achieved")
    boolean isAchieved,

    @JsonProperty("created_at")
    LocalDateTime createdAt
) {
  public static GoalAchievementResponseDto from(GoalRecord goalRecord) {
    return new GoalAchievementResponseDto(
        goalRecord.getRecordId(),
        goalRecord.getGoal().getGoalId(),
        goalRecord.getDiary().getDiaryId(),
        goalRecord.isAchieved(),
        goalRecord.getCreatedAt()
    );
  }
}