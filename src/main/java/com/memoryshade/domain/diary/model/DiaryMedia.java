package com.memoryshade.domain.diary.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diary_media")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    @Column(name = "media_url", nullable = false)
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;

    @Builder
    public DiaryMedia(Diary diary, String mediaUrl, MediaType mediaType) {
        this.diary = diary;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
    }
}
