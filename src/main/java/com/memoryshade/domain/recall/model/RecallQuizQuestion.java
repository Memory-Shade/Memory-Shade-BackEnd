package com.memoryshade.domain.recall.model;

import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.diary.model.DiaryMedia;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recall_quiz_questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecallQuizQuestion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long recallQuizQuestionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recall_quiz_session_id", nullable = false)
  private RecallQuizSession recallQuizSession;

  @Column(name = "question_order", nullable = false)
  private int questionOrder;

  @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
  private String questionText;

  @Column(name = "expected_answer", nullable = false, columnDefinition = "TEXT")
  private String expectedAnswer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "source_diary_id", nullable = false)
  private Diary sourceDiary;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "source_diary_media_id")
  private DiaryMedia sourceDiaryMedia;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "asked_at")
  private LocalDateTime askedAt;

  @Builder
  public RecallQuizQuestion(
      RecallQuizSession recallQuizSession,
      int questionOrder,
      String questionText,
      String expectedAnswer,
      Diary sourceDiary,
      DiaryMedia sourceDiaryMedia
  ) {
    this.recallQuizSession = recallQuizSession;
    this.questionOrder = questionOrder;
    this.questionText = questionText;
    this.expectedAnswer = expectedAnswer;
    this.sourceDiary = sourceDiary;
    this.sourceDiaryMedia = sourceDiaryMedia;
    this.createdAt = LocalDateTime.now();
  }

  public void markAsked() {
    if (this.askedAt == null) {
      this.askedAt = LocalDateTime.now();
    }
  }

  public String getReferenceMediaUrl() {
    return sourceDiaryMedia == null ? null : sourceDiaryMedia.getMediaUrl();
  }

  public boolean isAsked() {
    return askedAt != null;
  }
}