package com.memoryshade.domain.chat.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false)
    private SenderType senderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private ChatMessageType messageType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "reference_media_url")
    private String referenceMediaUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ChatMessage(
        ChatSession session,
        SenderType senderType,
        ChatMessageType messageType,
        String content,
        String referenceMediaUrl
    ) {
        this.session = session;
        this.senderType = senderType;
        this.messageType = messageType == null ? ChatMessageType.NORMAL : messageType;
        this.content = content;
        this.referenceMediaUrl = referenceMediaUrl;
        this.createdAt = LocalDateTime.now();
    }
}