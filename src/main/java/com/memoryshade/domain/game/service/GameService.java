package com.memoryshade.domain.game.service;

import com.memoryshade.domain.game.dto.GameCreateResultRequestDto;
import com.memoryshade.domain.game.dto.GameCreateResultResponseDto;
import com.memoryshade.domain.game.dto.GameResponseDto;
import com.memoryshade.domain.game.model.Game;
import com.memoryshade.domain.game.repository.GameRepository;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    @Transactional
    public GameCreateResultResponseDto createGameResult(Long loginUserId, GameCreateResultRequestDto request) {
        boolean isBestRecord = gameRepository.findTopByUser_UserIdOrderByScoreAsc(loginUserId)
                .map(bestGame -> request.score() < bestGame.getScore())
                .orElse(true);

        User user = userRepository.getByUserId(loginUserId);

        Game game = Game.builder()
                .user(user)
                .score(request.score())
                .build();

        gameRepository.save(game);
        return GameCreateResultResponseDto.fromGame(game, isBestRecord);
    }

    public GameResponseDto getBestGame(Long loginUserId) {
        Game game = gameRepository.getBestByUserId(loginUserId);
        return GameResponseDto.fromGame(game);
    }
}
