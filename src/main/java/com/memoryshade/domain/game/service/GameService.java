package com.memoryshade.domain.game.service;

import com.memoryshade.domain.game.dto.GameCreateResultRequestDto;
import com.memoryshade.domain.game.dto.GameCreateResultResponseDto;
import com.memoryshade.domain.game.dto.GameResponseDto;
import com.memoryshade.domain.game.dto.GameWeeklyAverageComparisonResponseDto;
import com.memoryshade.domain.game.model.Game;
import com.memoryshade.domain.game.repository.GameRepository;
import com.memoryshade.domain.guardianLink.exception.GuardianLinkErrorCode;
import com.memoryshade.domain.guardianLink.repository.GuardianLinkRepository;
import com.memoryshade.domain.user.model.Role;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import com.memoryshade.global.exception.ExceptionList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final GuardianLinkRepository guardianLinkRepository;

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

    public GameWeeklyAverageComparisonResponseDto getWeeklyGameAverageComparison(
        Long loginUserId,
        Long userId
    ) {
        validateGuardianCanReadGameDashboard(loginUserId, userId);

        LocalDate today = LocalDate.now();

        LocalDateTime todayStartDateTime = today.atStartOfDay();
        LocalDateTime tomorrowStartDateTime = today.plusDays(1).atStartOfDay();

        LocalDateTime averageStartDateTime = today.minusDays(7).atStartOfDay();
        LocalDateTime averageEndDateTime = today.atStartOfDay();

        Integer todayScore = gameRepository
            .findTopByUser_UserIdAndPlayedAtBetweenOrderByScoreAsc(
                userId,
                todayStartDateTime,
                tomorrowStartDateTime
            )
            .map(Game::getScore)
            .orElse(0);

        List<Game> averageGames = gameRepository.findAllByUser_UserIdAndPlayedAtBetween(
            userId,
            averageStartDateTime,
            averageEndDateTime
        );

        int averageScore = calculateAverageDailyBestScore(averageGames);
        boolean hasTodayData = todayScore > 0;
        boolean hasAverageData = !averageGames.isEmpty();

        int improvementRate = calculateImprovementRate(
            todayScore,
            averageScore,
            hasTodayData,
            hasAverageData
        );

        return new GameWeeklyAverageComparisonResponseDto(
            todayScore,
            averageScore,
            improvementRate,
            hasTodayData,
            buildGameDashboardDescription(improvementRate, hasTodayData, hasAverageData)
        );
    }

    private int calculateAverageDailyBestScore(List<Game> games) {
        if (games == null || games.isEmpty()) {
            return 0;
        }

        Map<LocalDate, Integer> dailyBestScores = new HashMap<>();

        for (Game game : games) {
            LocalDate playedDate = game.getPlayedAt().toLocalDate();
            dailyBestScores.merge(playedDate, game.getScore(), Math::min);
        }

        double averageScore = dailyBestScores.values()
            .stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);

        return Math.round((float) averageScore);
    }

    private int calculateImprovementRate(
        Integer todayScore,
        int averageScore,
        boolean hasTodayData,
        boolean hasAverageData
    ) {
        if (!hasTodayData || !hasAverageData || averageScore == 0) {
            return 0;
        }

        double improvementRate = ((averageScore - todayScore) / (double) averageScore) * 100.0;
        return Math.round((float) improvementRate);
    }

    private String buildGameDashboardDescription(
        int improvementRate,
        boolean hasTodayData,
        boolean hasAverageData
    ) {
        if (!hasTodayData) {
            return "오늘 인지 기능 게임 결과가 아직 없습니다";
        }

        if (!hasAverageData) {
            return "이전 기간 데이터가 없어 오늘 게임 결과만 표시합니다";
        }

        if (improvementRate > 0) {
            return "평균 대비 인지 기능 게임 결과가 %d%% 향상되었습니다".formatted(improvementRate);
        }

        if (improvementRate < 0) {
            return "평균 대비 인지 기능 게임 결과가 %d%% 감소했습니다".formatted(Math.abs(improvementRate));
        }

        return "평균 대비 인지 기능 게임 결과 변화 없음";
    }


    private void validateGuardianCanReadGameDashboard(Long loginUserId, Long userId) {
        if (loginUserId == null) {
            throw new ExceptionList(GuardianLinkErrorCode.UNAUTHORIZED_GUARDIAN);
        }

        User loginUser = userRepository.getByUserId(loginUserId);
        User targetUser = userRepository.getByUserId(userId);

        if (loginUser.getRole() != Role.GUARDIAN) {
            throw new ExceptionList(GuardianLinkErrorCode.UNAUTHORIZED_GUARDIAN);
        }

        if (targetUser.getRole() != Role.USER) {
            throw new ExceptionList(GuardianLinkErrorCode.TARGET_USER_ONLY);
        }

        guardianLinkRepository.validateLinked(userId, loginUserId);
    }
}