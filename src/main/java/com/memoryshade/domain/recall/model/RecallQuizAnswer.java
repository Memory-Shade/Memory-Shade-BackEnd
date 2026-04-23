package com.memoryshade.domain.recall.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recall_quiz_answers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecallQuizAnswer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long recallQuizAnswerId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recall_quiz_question_id", nullable = false, unique = true)
  private RecallQuizQuestion recallQuizQuestion;

  @Column(name = "user_answer", nullable = false, columnDefinition = "TEXT")
  private String userAnswer;

  @Enumerated(EnumType.STRING)
  @Column(name = "judgement", nullable = false)
  private RecallQuizJudgement judgement;

  @Column(name = "evaluation_reason", columnDefinition = "TEXT")
  private String evaluationReason;

  @Column(name = "answered_at", nullable = false, updatable = false)
  private LocalDateTime answeredAt;

  @Builder
  public RecallQuizAnswer(
      RecallQuizQuestion recallQuizQuestion,
      String userAnswer,
      RecallQuizJudgement judgement,
      String evaluationReason
  ) {
    this.recallQuizQuestion = recallQuizQuestion;
    this.userAnswer = userAnswer;
    this.judgement = judgement;
    this.evaluationReason = evaluationReason;
    this.answeredAt = LocalDateTime.now();
  }
}