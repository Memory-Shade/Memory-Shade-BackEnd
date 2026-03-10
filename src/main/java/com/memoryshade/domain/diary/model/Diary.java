package com.memoryshade.domain.diary.model;

import com.memoryshade.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "diaries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "content_stt", columnDefinition = "TEXT")
    private String contentStt;

    @Column(name = "content_summary", columnDefinition = "TEXT")
    private String contentSummary;

    @Column(name = "diary_date", nullable = false)
    private LocalDate diaryDate;

    @Column(name = "is_shared", nullable = false)
    private boolean isShared;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Diary(User user, String contentStt, String contentSummary, LocalDate diaryDate) {
        this.user = user;
        this.contentStt = contentStt;
        this.contentSummary = contentSummary;
        this.diaryDate = diaryDate;
        this.isShared = false;
        this.createdAt = LocalDateTime.now();
    }

    public void share() {
        this.isShared = true;
    }

    public void unshare() {
        this.isShared = false;
    }
}
