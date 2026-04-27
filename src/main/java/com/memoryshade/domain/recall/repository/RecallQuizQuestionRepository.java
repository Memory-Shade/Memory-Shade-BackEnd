package com.memoryshade.domain.recall.repository;

import com.memoryshade.domain.recall.model.RecallQuizQuestion;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface RecallQuizQuestionRepository extends Repository<RecallQuizQuestion, Long> {

  List<RecallQuizQuestion> saveAll(Iterable<RecallQuizQuestion> questions);

  List<RecallQuizQuestion> findAllByRecallQuizSession_RecallQuizSessionIdOrderByQuestionOrderAsc(Long recallQuizSessionId);
}