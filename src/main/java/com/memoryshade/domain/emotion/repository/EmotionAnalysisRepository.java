package com.memoryshade.domain.emotion.repository;

import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.emotion.model.EmotionAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; // [추가] Optional 임포트

@Repository
public interface EmotionAnalysisRepository extends JpaRepository<EmotionAnalysis, Long> {
  Optional<EmotionAnalysis> findByDiary(Diary diary);
}
