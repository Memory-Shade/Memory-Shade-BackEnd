package com.memoryshade.domain.diary.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer diaryId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "content_stt")
    private String contentStt; //TODO: 자료명 수정

    @Column(name = "content_summary")
    private String contentSummary; //TODO: 자료명 수정

    @Column(name = "diary_date")
    private LocalDateTime diaryDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_shared")
    private boolean isShared;

    @Builder
    public Diary(String contentStt, String contentSummary, LocalDateTime diaryDate, LocalDateTime createdAt, boolean isShared) {
        this.contentStt = contentStt;
        this.contentSummary = contentSummary;
        this.diaryDate = diaryDate;
        this.createdAt = createdAt;
        this.isShared = isShared;
    }
}
