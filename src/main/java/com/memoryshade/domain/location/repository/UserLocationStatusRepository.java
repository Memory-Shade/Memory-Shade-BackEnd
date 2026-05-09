package com.memoryshade.domain.location.repository;

import com.memoryshade.domain.location.model.UserLocationStatus;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserLocationStatusRepository extends Repository<UserLocationStatus, Long> {

    UserLocationStatus save(UserLocationStatus userLocationStatus);

    Optional<UserLocationStatus> findByUser_UserId(Long userId);
}
