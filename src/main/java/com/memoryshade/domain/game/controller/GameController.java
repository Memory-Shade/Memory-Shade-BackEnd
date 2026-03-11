package com.memoryshade.domain.game.controller;

import com.memoryshade.domain.game.dto.GameResponseDto;
import com.memoryshade.domain.game.dto.GameUpdateScoreRequestDto;
import com.memoryshade.domain.game.dto.GameUpdateScoreResponseDto;
import com.memoryshade.domain.game.service.GameService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameResponseDto> createGame(
            @AuthenticationPrincipal Long loginUserId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.createGame(loginUserId));
    }

    @PatchMapping("/{gameId}/result")
    public ResponseEntity<GameUpdateScoreResponseDto> updateScore(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long gameId,
            @Valid @RequestBody GameUpdateScoreRequestDto request) {
        return ResponseEntity.status(HttpStatus.OK).body(gameService.updateScore(loginUserId, gameId, request));
    }
}
