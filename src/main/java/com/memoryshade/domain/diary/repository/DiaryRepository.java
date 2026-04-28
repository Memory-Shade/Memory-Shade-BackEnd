package com.memoryshade.domain.diary.repository;

import com.memoryshade.domain.diary.exception.DiaryErrorCode;
import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.global.exception.ExceptionList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends Repository<Diary, Long> {
    Diary save(Diary diary);

    Optional<Diary> findByDiaryIdAndUserUserId(Long diaryId, Long userId);

    default Diary getDiary(Long diaryId, Long userId) {
        return findByDiaryIdAndUserUserId(diaryId, userId)
            .orElseThrow(() -> new ExceptionList(DiaryErrorCode.DIARY_NOT_FOUND));
    }

    List<Diary> findAllByUser_UserIdAndDiaryDate(Long userId, LocalDate date);

    List<Diary> findAllByUser_UserIdAndDiaryDateAndIsSharedTrue(Long userId, LocalDate date);

    List<Diary> findAllByUser_UserIdAndDiaryDateBetweenOrderByDiaryDateAsc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Diary> findAllByUser_UserIdAndDiaryDateBetweenAndIsSharedTrueOrderByDiaryDateAsc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Diary> findAllByUser_UserIdAndDiaryDateBetweenOrderByDiaryDateDesc(
        Long userId,
        LocalDate startDate,
        LocalDate endDate
    );

    @Query("""
            select distinct d.diaryDate
            from Diary d
            where d.user.userId = :userId
              and d.diaryDate <= :endDate
            order by d.diaryDate desc
            """)
    List<LocalDate> findDistinctDiaryDatesByUserIdUpTo(
            @Param("userId") Long userId,
            @Param("endDate") LocalDate endDate
    );

    Optional<Diary> findTopByUser_UserIdAndDiaryDateOrderByCreatedAtDesc(Long userId, LocalDate date);

    Optional<Diary> findById(Long diaryId);
}
