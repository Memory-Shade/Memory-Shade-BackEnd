package com.memoryshade.domain.diary.service;

import com.memoryshade.domain.diary.dto.DiaryCreateFromChatResponseDto;
import com.memoryshade.domain.diary.dto.DiaryReadResponseDto;
import com.memoryshade.domain.diary.dto.DiaryStreakResponseDto;
import com.memoryshade.domain.diary.dto.DiaryUpdateShareResponseDto;
import com.memoryshade.domain.diary.exception.DiaryErrorCode;
import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.diary.repository.DiaryRepository;
import com.memoryshade.domain.guardianLink.exception.GuardianLinkErrorCode;
import com.memoryshade.domain.guardianLink.repository.GuardianLinkRepository;
import com.memoryshade.domain.notification.service.NotificationService;
import com.memoryshade.domain.user.model.Role;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import com.memoryshade.global.exception.ExceptionList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final GuardianLinkRepository guardianLinkRepository;
    private final NotificationService notificationService;

    public List<DiaryReadResponseDto> getAllDiariesByDate(Long loginUserId, LocalDate date) {
        userRepository.getByUserId(loginUserId);

        return diaryRepository.findAllByUser_UserIdAndDiaryDate(loginUserId, date)
                .stream()
                .map(DiaryReadResponseDto::fromDiary)
                .toList();
    }

    public List<DiaryReadResponseDto> getAllDiariesByDateRange(
            Long loginUserId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        userRepository.getByUserId(loginUserId);
        validateDateRange(startDate, endDate);

        return diaryRepository.findAllByUser_UserIdAndDiaryDateBetweenOrderByDiaryDateAsc(
                        loginUserId,
                        startDate,
                        endDate
                )
                .stream()
                .map(DiaryReadResponseDto::fromDiary)
                .toList();
    }

    public DiaryStreakResponseDto getMyDiaryStreak(Long loginUserId) {
        userRepository.getByUserId(loginUserId);

        LocalDate today = LocalDate.now();
        List<LocalDate> diaryDates = diaryRepository.findDistinctDiaryDatesByUserIdUpTo(
                loginUserId,
                today
        );
        int streakDays = calculateCurrentStreakDays(diaryDates, today);
        LocalDate lastDiaryDate = diaryDates.isEmpty() ? null : diaryDates.get(0);

        return new DiaryStreakResponseDto(streakDays, today, lastDiaryDate);
    }

    public List<DiaryReadResponseDto> getUserSharedDiariesByDate(
            Long loginGuardianId,
            Long userId,
            LocalDate date
    ) {
        if (loginGuardianId == null) {
            throw new ExceptionList(GuardianLinkErrorCode.UNAUTHORIZED_GUARDIAN);
        }

        User guardian = userRepository.getByUserId(loginGuardianId);
        if (guardian.getRole() != Role.GUARDIAN) {
            throw new ExceptionList(GuardianLinkErrorCode.GUARDIAN_ONLY);
        }

        User user = userRepository.getByUserId(userId);
        if (user.getRole() != Role.USER) {
            throw new ExceptionList(GuardianLinkErrorCode.TARGET_USER_ONLY);
        }

        guardianLinkRepository.validateLinked(userId, loginGuardianId);

        return diaryRepository.findAllByUser_UserIdAndDiaryDateAndIsSharedTrue(userId, date)
                .stream()
                .map(DiaryReadResponseDto::fromDiary)
                .toList();
    }

    public List<DiaryReadResponseDto> getUserSharedDiariesByDateRange(
            Long loginGuardianId,
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (loginGuardianId == null) {
            throw new ExceptionList(GuardianLinkErrorCode.UNAUTHORIZED_GUARDIAN);
        }

        validateDateRange(startDate, endDate);

        User guardian = userRepository.getByUserId(loginGuardianId);
        if (guardian.getRole() != Role.GUARDIAN) {
            throw new ExceptionList(GuardianLinkErrorCode.GUARDIAN_ONLY);
        }

        User user = userRepository.getByUserId(userId);
        if (user.getRole() != Role.USER) {
            throw new ExceptionList(GuardianLinkErrorCode.TARGET_USER_ONLY);
        }

        guardianLinkRepository.validateLinked(userId, loginGuardianId);

        return diaryRepository.findAllByUser_UserIdAndDiaryDateBetweenAndIsSharedTrueOrderByDiaryDateAsc(
                        userId,
                        startDate,
                        endDate
                )
                .stream()
                .map(DiaryReadResponseDto::fromDiary)
                .toList();
    }


    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new ExceptionList(DiaryErrorCode.INVALID_DATE_RANGE);
        }
    }

    private int calculateCurrentStreakDays(List<LocalDate> diaryDates, LocalDate today) {
        if (diaryDates.isEmpty()) {
            return 0;
        }

        LocalDate latestDiaryDate = diaryDates.get(0);
        LocalDate expectedDate = latestDiaryDate.isEqual(today) ? today : today.minusDays(1);
        if (latestDiaryDate.isBefore(expectedDate)) {
            return 0;
        }

        int streakDays = 0;
        for (LocalDate diaryDate : diaryDates) {
            if (diaryDate.isEqual(expectedDate)) {
                ++streakDays;
                expectedDate = expectedDate.minusDays(1);
                continue;
            }

            if (diaryDate.isBefore(expectedDate)) {
                break;
            }
        }

        return streakDays;
    }

    @Transactional
    public DiaryUpdateShareResponseDto updateDiaryShare(Long loginUserId, Long diaryId) {
        User user = userRepository.getByUserId(loginUserId);

        Diary diary = diaryRepository.getDiary(diaryId, loginUserId);
        if (diary.isShared()) {
            diary.unshare();
        } else {
            diary.share();
            notificationService.createDiarySharedNotifications(
                    user.getUserId(),
                    user.getName(),
                    diary.getDiaryDate()
            );
        }
        return DiaryUpdateShareResponseDto.fromDiary(diary);
    }

    public DiaryCreateFromChatResponseDto createDiaryFromChat(
        Long loginUserId,
        String contentStt,
        String contentSummary,
        LocalDate diaryDate
    ) {
        User user = userRepository.getByUserId(loginUserId);

        Diary diary = Diary.builder()
            .user(user)
            .contentStt(contentStt)
            .contentSummary(contentSummary)
            .diaryDate(diaryDate)
            .build();

        Diary savedDiary = diaryRepository.save(diary);

        return DiaryCreateFromChatResponseDto.fromDiary(savedDiary);
    }

    public Optional<Diary> findTopDiaryByUserIdAndDiaryDate(Long userId, LocalDate date) {
        return diaryRepository.findTopByUser_UserIdAndDiaryDateOrderByCreatedAtDesc(userId, date);
    }
}
