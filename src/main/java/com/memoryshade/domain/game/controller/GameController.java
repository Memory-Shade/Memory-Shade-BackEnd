package com.memoryshade.domain.game.controller;

import com.memoryshade.domain.game.dto.GameCreateResultRequestDto;
import com.memoryshade.domain.game.dto.GameCreateResultResponseDto;
import com.memoryshade.domain.game.dto.GameResponseDto;
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

    @PostMapping("/result")
    public ResponseEntity<GameCreateResultResponseDto> createGameResult(
            @AuthenticationPrincipal Long loginUserId,
            @Valid @RequestBody GameCreateResultRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gameService.createGameResult(loginUserId, request));
    }

    @GetMapping("/me/best")
    public ResponseEntity<GameResponseDto> getBestGame(
            @AuthenticationPrincipal Long loginUserId) {
        return ResponseEntity.ok(gameService.getBestGame(loginUserId));
    }
}

