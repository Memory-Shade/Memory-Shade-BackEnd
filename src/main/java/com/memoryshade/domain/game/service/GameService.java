package com.memoryshade.domain.game.service;

import com.memoryshade.domain.game.dto.GameResponseDto;
import com.memoryshade.domain.game.dto.GameUpdateScoreRequestDto;
import com.memoryshade.domain.game.dto.GameUpdateScoreResponseDto;
import com.memoryshade.domain.game.model.Game;
import com.memoryshade.domain.game.repository.GameRepository;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public GameResponseDto createGame(Long loginUserId) {
        User user = userRepository.getByUserId(loginUserId);

        Game game = Game.builder()
                        .user(user)
                        .score(0)
                        .build();

        gameRepository.save(game);
        return GameResponseDto.fromGame(game);
    }

    public GameUpdateScoreResponseDto updateScore(
            Long loginUserId,
            Long gameId,
            GameUpdateScoreRequestDto request) {
        Game game = gameRepository.getByGameIdAndUserId(gameId, loginUserId);
        game.updateScore(request.score());
        return GameUpdateScoreResponseDto.fromGame(game);
    }
}
