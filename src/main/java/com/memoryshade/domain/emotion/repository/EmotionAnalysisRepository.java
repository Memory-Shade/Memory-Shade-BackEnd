package com.memoryshade.domain.emotion.repository;

import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.emotion.model.EmotionAnalysis;
import org.springframework.data.repository.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmotionAnalysisRepository extends Repository<EmotionAnalysis, Long> {

  EmotionAnalysis save(EmotionAnalysis emotionAnalysis);

  Optional<EmotionAnalysis> findByDiary(Diary diary);

  List<EmotionAnalysis> findAllByDiary_User_UserIdAndDiary_DiaryDateBetween(
      Long userId,
      LocalDate startDate,
      LocalDate endDate
  );
}