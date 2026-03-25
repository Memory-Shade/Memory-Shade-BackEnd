package com.memoryshade.domain.diary.service;

import com.memoryshade.domain.chat.model.ChatSessionMedia;
import com.memoryshade.domain.diary.dto.DiaryMediaReadResponseDto;
import com.memoryshade.domain.diary.exception.DiaryErrorCode;
import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.diary.model.DiaryMedia;
import com.memoryshade.domain.diary.repository.DiaryMediaRepository;
import com.memoryshade.domain.diary.repository.DiaryRepository;
import com.memoryshade.domain.guardianLink.exception.GuardianLinkErrorCode;
import com.memoryshade.domain.guardianLink.repository.GuardianLinkRepository;
import com.memoryshade.domain.user.model.Role;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import com.memoryshade.global.exception.ExceptionList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryMediaService {

  private final DiaryMediaRepository diaryMediaRepository;
  private final DiaryRepository diaryRepository;
  private final UserRepository userRepository;
  private final GuardianLinkRepository guardianLinkRepository;

  public void createDiaryMediasFromChatSession(Long diaryId, List<ChatSessionMedia> chatSessionMedias) {
    Diary diary = diaryRepository.findById(diaryId)
        .orElseThrow(() -> new ExceptionList(DiaryErrorCode.DIARY_NOT_FOUND));

    for (ChatSessionMedia chatSessionMedia : chatSessionMedias) {
      diaryMediaRepository.save(
          DiaryMedia.builder()
              .diary(diary)
              .mediaUrl(chatSessionMedia.getMediaUrl())
              .mediaType(chatSessionMedia.getMediaType())
              .build()
      );
    }
  }

  @Transactional(readOnly = true)
  public List<DiaryMediaReadResponseDto> getDiaryMedias(Long loginUserId, Long diaryId) {
    User loginUser = userRepository.getByUserId(loginUserId);

    Diary diary = diaryRepository.findById(diaryId)
        .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

    if (loginUser.getRole() == Role.USER) {
      if (!diary.getUser().getUserId().equals(loginUserId)) {
        throw new ExceptionList(GuardianLinkErrorCode.TARGET_USER_ONLY);
      }
    } else if (loginUser.getRole() == Role.GUARDIAN) {
      guardianLinkRepository.validateLinked(diary.getUser().getUserId(), loginUserId);

      if (!diary.isShared()) {
        throw new IllegalArgumentException("공유되지 않은 일기입니다.");
      }
    }

    return diaryMediaRepository.findAllByDiary_DiaryId(diaryId)
        .stream()
        .map(DiaryMediaReadResponseDto::fromDiaryMedia)
        .toList();
  }
}