package com.memoryshade.domain.location.repository;

import com.memoryshade.domain.location.model.WanderingEvent;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;

public interface WanderingEventRepository extends Repository<WanderingEvent, Long> {

    WanderingEvent save(WanderingEvent wanderingEvent);

    long countByUser_UserId(Long userId);

    long countByUser_UserIdAndOccurredAtBetween(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
