package com.memoryshade.domain.diary.service;

import com.memoryshade.domain.diary.dto.DiaryReadResponseDto;
import com.memoryshade.domain.diary.dto.DiaryUpdateShareResponseDto;
import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.diary.repository.DiaryRepository;
import com.memoryshade.domain.guardianLink.exception.GuardianLinkErrorCode;
import com.memoryshade.domain.guardianLink.repository.GuardianLinkRepository;
import com.memoryshade.domain.user.model.Role;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import com.memoryshade.global.exception.ExceptionList;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // [추가] 로그용
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j // [추가]
@Service
@RequiredArgsConstructor
@Transactional
public class DiaryService {

  private final DiaryRepository diaryRepository;
  private final UserRepository userRepository;
  private final GuardianLinkRepository guardianLinkRepository;

  public Diary saveDiary(User user, String content) {
    return diaryRepository.save(Diary.builder()
        .user(user)
        .contentStt(content)
        .diaryDate(LocalDate.now())
        .build());
  }

  public List<DiaryReadResponseDto> getAllDiariesByDate(Long loginUserId, LocalDate date) {
    userRepository.getByUserId(loginUserId);

    return diaryRepository.findAllByUser_UserIdAndDiaryDate(loginUserId, date)
        .stream()
        .map(DiaryReadResponseDto::fromDiary)
        .toList();
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

  public DiaryUpdateShareResponseDto updateDiaryShare(Long loginUserId, Long diaryId) {
    userRepository.getByUserId(loginUserId);

    Diary diary = diaryRepository.getDiary(diaryId, loginUserId);
    if (diary.isShared()) {
      diary.unshare();
    } else {
      diary.share();
    }
    return DiaryUpdateShareResponseDto.fromDiary(diary);
  }
}