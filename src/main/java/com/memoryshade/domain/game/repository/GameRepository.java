package com.memoryshade.domain.game.repository;

import com.memoryshade.domain.game.exception.GameErrorCode;
import com.memoryshade.domain.game.model.Game;
import com.memoryshade.global.exception.ExceptionList;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends Repository<Game, Long> {

    Game save(Game gameSession);

    Optional<Game> findByGameIdAndUser_UserId(Long gameId, Long userId);

    Optional<Game> findTopByUser_UserIdOrderByScoreAsc(Long userId);

    Optional<Game> findTopByUser_UserIdAndPlayedAtBetweenOrderByScoreAsc(
        Long userId,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );

    List<Game> findAllByUser_UserIdAndPlayedAtBetween(
        Long userId,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );

    default Game getByGameIdAndUserId(Long gameId, Long userId) {
        return findByGameIdAndUser_UserId(gameId, userId)
            .orElseThrow(() -> new ExceptionList(GameErrorCode.GAME_NOT_FOUND));
    }

    default Game getBestByUserId(Long userId) {
        return findTopByUser_UserIdOrderByScoreAsc(userId)
            .orElseThrow(() -> new ExceptionList(GameErrorCode.GAME_NOT_FOUND));
    }
}