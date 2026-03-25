package com.memoryshade.domain.chat.repository;

import java.util.List;

import com.memoryshade.domain.chat.model.ChatSessionMedia;
import org.springframework.data.repository.Repository;

public interface ChatSessionMediaRepository extends Repository<ChatSessionMedia, Long> {

  ChatSessionMedia save(ChatSessionMedia chatSessionMedia);

  List<ChatSessionMedia> findAllBySession_SessionIdOrderByCreatedAtAsc(Long sessionId);

  void deleteAll(Iterable<ChatSessionMedia> chatSessionMedias);
}