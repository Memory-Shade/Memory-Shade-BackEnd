package com.memoryshade.domain.recall.model;

import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.diary.model.DiaryMedia;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
  }

  public String getReferenceMediaUrl() {
    return sourceDiaryMedia == null ? null : sourceDiaryMedia.getMediaUrl();
  }
}