package com.memoryshade.domain.chat.repository;

import java.util.List;
import java.util.Optional;


import com.memoryshade.domain.chat.model.ChatMessage;
import org.springframework.data.repository.Repository;

public interface ChatMessageRepository extends Repository<ChatMessage, Long> {
  ChatMessage save(ChatMessage chatMessage);
  List<ChatMessage> findAllBySession_SessionIdOrderByCreatedAtAsc(Long sessionId);
  Optional<ChatMessage> findByMessageIdAndSession_SessionId(Long messageId, Long sessionId);

}