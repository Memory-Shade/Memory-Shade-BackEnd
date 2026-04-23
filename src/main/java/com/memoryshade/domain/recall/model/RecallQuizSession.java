package com.memoryshade.domain.recall.model;

import com.memoryshade.domain.chat.model.ChatSession;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recall_quiz_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecallQuizSession {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long recallQuizSessionId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "session_id", nullable = false, unique = true)
  private ChatSession session;

  @Column(name = "total_question_count", nullable = false)
  private int totalQuestionCount;

  @Column(name = "correct_count", nullable = false)
  private int correctCount;

  @Column(name = "partial_count", nullable = false)
  private int partialCount;

  @Column(name = "score", nullable = false)
  private int score;

  @Column(name = "is_completed", nullable = false)
  private boolean isCompleted;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @Builder
  public RecallQuizSession(ChatSession session, int totalQuestionCount) {
    this.session = session;
    this.totalQuestionCount = totalQuestionCount;
    this.correctCount = 0;
    this.partialCount = 0;
    this.score = 0;
    this.isCompleted = false;
    this.createdAt = LocalDateTime.now();
  }

  public void applyJudgement(RecallQuizJudgement judgement) {
    if (judgement == RecallQuizJudgement.CORRECT) {
      this.correctCount += 1;
    } else if (judgement == RecallQuizJudgement.PARTIAL) {
      this.partialCount += 1;
    }

    int rawScore = (this.correctCount * 100) + (this.partialCount * 50);
    this.score = this.totalQuestionCount == 0 ? 0 : rawScore / this.totalQuestionCount;
  }

  public void complete() {
    this.isCompleted = true;
    this.completedAt = LocalDateTime.now();
  }
}