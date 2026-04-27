package com.memoryshade.domain.recall.repository;

import com.memoryshade.domain.recall.model.RecallQuizSession;
import org.springframework.data.repository.Repository;

import java.time.LocalDate;
import java.util.Optional;

public interface RecallQuizSessionRepository extends Repository<RecallQuizSession, Long> {

  RecallQuizSession save(RecallQuizSession recallQuizSession);

  Optional<RecallQuizSession> findById(Long recallQuizSessionId);

  Optional<RecallQuizSession> findByUser_UserIdAndQuizDateAndIsCompletedFalse(Long userId, LocalDate quizDate);
}