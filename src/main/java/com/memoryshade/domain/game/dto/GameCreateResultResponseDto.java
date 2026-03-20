package com.memoryshade.domain.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memoryshade.domain.game.model.Game;

import java.time.LocalDateTime;

public record GameCreateResultResponseDto(

        @JsonProperty("game_id")
        Long gameId,

        @JsonProperty("user_id")
        Long userId,
        Integer score,

        @JsonProperty("played_at")
        LocalDateTime playedAt,

        @JsonProperty("is_best_record")
        boolean isBestRecord
) {
    public static GameCreateResultResponseDto fromGame(Game game, boolean isBestRecord) {
        return new GameCreateResultResponseDto(
                game.getGameId(),
                game.getUser().getUserId(),
                game.getScore(),
                game.getPlayedAt(),
                isBestRecord
        );
    }
}
