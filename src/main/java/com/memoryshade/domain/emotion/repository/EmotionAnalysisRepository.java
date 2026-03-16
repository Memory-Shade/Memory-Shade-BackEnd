package com.memoryshade.domain.emotion.repository;

import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.emotion.model.EmotionAnalysis;
import org.springframework.data.repository.Repository; // 기본 Repository 사용
import java.util.Optional;

public interface EmotionAnalysisRepository extends Repository<EmotionAnalysis, Long> {
  EmotionAnalysis save(EmotionAnalysis emotionAnalysis);

  Optional<EmotionAnalysis> findByDiary(Diary diary);

  void delete(EmotionAnalysis emotionAnalysis);

  void flush();
}