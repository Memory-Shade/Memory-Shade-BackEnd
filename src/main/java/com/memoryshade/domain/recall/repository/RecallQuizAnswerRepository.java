package com.memoryshade.domain.recall.repository;

import com.memoryshade.domain.recall.model.RecallQuizAnswer;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface RecallQuizAnswerRepository extends Repository<RecallQuizAnswer, Long> {

  RecallQuizAnswer save(RecallQuizAnswer recallQuizAnswer);

  boolean existsByRecallQuizQuestion_RecallQuizQuestionId(Long recallQuizQuestionId);

  Optional<RecallQuizAnswer> findByRecallQuizQuestion_RecallQuizQuestionId(Long recallQuizQuestionId);
}