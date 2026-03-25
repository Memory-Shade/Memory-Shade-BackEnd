package com.memoryshade.domain.chat.model;

import java.time.LocalDateTime;

import com.memoryshade.domain.diary.model.MediaType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_session_media")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatSessionMedia {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long chatSessionMediaId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "session_id", nullable = false)
  private ChatSession session;

  @Column(name = "media_url", nullable = false)
  private String mediaUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "media_type", nullable = false)
  private MediaType mediaType;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Builder
  public ChatSessionMedia(ChatSession session, String mediaUrl, MediaType mediaType) {
    this.session = session;
    this.mediaUrl = mediaUrl;
    this.mediaType = mediaType;
    this.createdAt = LocalDateTime.now();
  }
}