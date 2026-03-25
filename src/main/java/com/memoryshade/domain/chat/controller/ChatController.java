package com.memoryshade.domain.chat.controller;

import java.util.List;

import com.memoryshade.domain.chat.dto.ChatMediaUploadResponseDto;
import com.memoryshade.domain.chat.dto.ChatMessageResponseDto;
import com.memoryshade.domain.chat.dto.ChatSessionCloseResponseDto;
import com.memoryshade.domain.chat.dto.ChatSessionCreateResponseDto;
import com.memoryshade.domain.chat.dto.ChatVoiceResponseDto;
import com.memoryshade.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/chat-sessions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")

public class ChatController {

  private final ChatService chatService;

  @PostMapping
  public ResponseEntity<ChatSessionCreateResponseDto> createChatSession(
      @AuthenticationPrincipal Long loginUserId
  ) {
    return ResponseEntity.ok(chatService.createChatSession(loginUserId));
  }


  @PostMapping(
      value = "/{sessionId}/messages/voice",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<ChatVoiceResponseDto> createVoiceChatMessage(
      @AuthenticationPrincipal Long loginUserId,
      @PathVariable Long sessionId,
      @RequestPart("file") MultipartFile file
  ) {
    return ResponseEntity.ok(
        chatService.createVoiceChatMessage(loginUserId, sessionId, file)
    );
  }

  @PostMapping(
      value = "/{sessionId}/media",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<ChatMediaUploadResponseDto> uploadChatMedia(
      @AuthenticationPrincipal Long loginUserId,
      @PathVariable Long sessionId,
      @RequestPart("file") MultipartFile file
  ) {
    return ResponseEntity.ok(
        chatService.uploadChatMedia(loginUserId, sessionId, file)
    );
  }

  @PostMapping("/{sessionId}/close")
  public ResponseEntity<ChatSessionCloseResponseDto> closeChatSession(
      @AuthenticationPrincipal Long loginUserId,
      @PathVariable Long sessionId
  ) {
    return ResponseEntity.ok(
        chatService.closeChatSession(loginUserId, sessionId)
    );
  }

  @GetMapping("/{sessionId}/messages")
  public ResponseEntity<List<ChatMessageResponseDto>> getChatMessages(
      @AuthenticationPrincipal Long loginUserId,
      @PathVariable Long sessionId
  ) {
    return ResponseEntity.ok(chatService.getChatMessages(loginUserId, sessionId));
  }

}