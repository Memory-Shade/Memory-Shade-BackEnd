package com.memoryshade.domain.diary.repository;

import com.memoryshade.domain.diary.model.DiaryMedia;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface DiaryMediaRepository extends Repository<DiaryMedia, Long> {

  DiaryMedia save(DiaryMedia diaryMedia);

  List<DiaryMedia> findAllByDiary_DiaryId(Long diaryId);
}