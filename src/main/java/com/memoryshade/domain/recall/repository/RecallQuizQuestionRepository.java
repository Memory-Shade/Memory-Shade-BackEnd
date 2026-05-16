package com.memoryshade.domain.recall.repository;

import com.memoryshade.domain.recall.model.RecallQuizQuestion;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;


public interface RecallQuizQuestionRepository extends Repository<RecallQuizQuestion, Long> {

  List<RecallQuizQuestion> saveAll(Iterable<RecallQuizQuestion> questions);

  List<RecallQuizQuestion> findAllByRecallQuizSession_RecallQuizSessionIdOrderByQuestionOrderAsc(Long recallQuizSessionId);

  Optional<RecallQuizQuestion> findByRecallQuizQuestionIdAndRecallQuizSession_RecallQuizSessionId(
      Long recallQuizQuestionId,
      Long recallQuizSessionId
  );
}