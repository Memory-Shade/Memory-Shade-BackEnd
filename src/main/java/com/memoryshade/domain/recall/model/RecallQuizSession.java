package com.memoryshade.domain.recall.model;

import com.memoryshade.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "recall_quiz_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecallQuizSession {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long recallQuizSessionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "quiz_date", nullable = false)
  private LocalDate quizDate;

  @Column(name = "total_question_count", nullable = false)
  private int totalQuestionCount;

  @Column(name = "correct_count", nullable = false)
  private int correctCount;

  @Column(name = "partial_count", nullable = false)
  private int partialCount;

  @Column(name = "is_completed", nullable = false)
  private boolean isCompleted;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @Builder
  public RecallQuizSession(User user, LocalDate quizDate, int totalQuestionCount) {
    this.user = user;
    this.quizDate = quizDate;
    this.totalQuestionCount = totalQuestionCount;
    this.correctCount = 0;
    this.partialCount = 0;
    this.isCompleted = false;
    this.createdAt = LocalDateTime.now();
  }

  public void applyJudgement(RecallQuizJudgement judgement) {
    if (judgement == RecallQuizJudgement.CORRECT) {
      this.correctCount++;
      return;
    }

    if (judgement == RecallQuizJudgement.PARTIAL) {
      this.partialCount++;
    }
  }

  public void complete() {
    this.isCompleted = true;
    this.completedAt = LocalDateTime.now();
  }

  public double calculateScorePercent() {
    if (totalQuestionCount == 0) {
      return 0.0;
    }

    double earnedScore = correctCount + (partialCount * 0.5);
    return (earnedScore * 100.0) / totalQuestionCount;
  }
}