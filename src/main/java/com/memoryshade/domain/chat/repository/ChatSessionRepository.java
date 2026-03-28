package com.memoryshade.domain.chat.repository;

import java.time.LocalDate;
import java.util.Optional;

import com.memoryshade.domain.chat.model.ChatSession;
import org.springframework.data.repository.Repository;

public interface ChatSessionRepository extends Repository<ChatSession, Long> {
  ChatSession save(ChatSession chatSession);
  Optional<ChatSession> findById(Long sessionId);
  Optional<ChatSession> findByUser_UserIdAndSessionDateAndIsActiveTrue(Long userId, LocalDate sessionDate);
}