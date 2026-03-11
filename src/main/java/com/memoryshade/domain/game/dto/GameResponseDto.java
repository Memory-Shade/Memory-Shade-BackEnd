package com.memoryshade.domain.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memoryshade.domain.game.model.Game;

import java.time.LocalDateTime;

public record GameResponseDto(

        @JsonProperty("game_id")
        Long gameId,

        @JsonProperty("user_id")
        Long userId,
        Integer score,

        @JsonProperty("played_at")
        LocalDateTime playedAt
) {
    public static GameResponseDto fromGame(Game game) {
        return new GameResponseDto(
                game.getGameId(),
                game.getUser().getUserId(),
                game.getScore(),
                game.getPlayedAt()
        );
    }
}
