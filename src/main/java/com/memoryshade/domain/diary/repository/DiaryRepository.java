package com.memoryshade.domain.diary.repository;

import com.memoryshade.domain.diary.exception.DiaryErrorCode;
import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.global.exception.ExceptionList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Optional<Diary> findByDiaryIdAndUserUserId(Long diaryId, Long userId);

    default Diary getDiary(Long diaryId, Long userId) {
        return findByDiaryIdAndUserUserId(diaryId, userId)
                .orElseThrow(() -> new ExceptionList(DiaryErrorCode.DIARY_NOT_FOUND));
    }

    List<Diary> findAllByUser_UserIdAndDiaryDate(Long userId, LocalDate date);

    List<Diary> findAllByUser_UserIdAndDiaryDateAndIsSharedTrue(Long userId, LocalDate date);

    Optional<Diary> findTopByUser_UserIdAndDiaryDateOrderByCreatedAtDesc(Long userId, LocalDate date);
}
