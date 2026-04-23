package com.memoryshade.domain.chat.model;

import com.memoryshade.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "conversation_stage", nullable = false)
    private ConversationStage conversationStage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ChatSession(User user, LocalDate sessionDate, ConversationStage conversationStage) {
        this.user = user;
        this.sessionDate = sessionDate;
        this.isActive = true;
        this.conversationStage = conversationStage == null
            ? ConversationStage.DAILY_RECORD
            : conversationStage;
        this.createdAt = LocalDateTime.now();
    }

    public void close() {
        this.isActive = false;
    }

    public void changeToRecallQuizStage() {
        this.conversationStage = ConversationStage.RECALL_QUIZ;
    }

    public void changeToDailyRecordStage() {
        this.conversationStage = ConversationStage.DAILY_RECORD;
    }
}