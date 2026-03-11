package com.memoryshade.domain.game.dto;

import com.memoryshade.domain.game.model.Game;

public record GameUpdateScoreResponseDto(
        Integer score
) {
    public static GameUpdateScoreResponseDto fromGame(Game game) {
        return new GameUpdateScoreResponseDto(game.getScore());
    }
}
