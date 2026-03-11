package com.memoryshade.domain.guardianLink.repository;

import com.memoryshade.domain.guardianLink.exception.GuardianLinkErrorCode;
import com.memoryshade.domain.guardianLink.model.GuardianLink;
import com.memoryshade.global.exception.ExceptionList;
import org.springframework.data.repository.Repository;

public interface GuardianLinkRepository extends Repository<GuardianLink, Long> {

    GuardianLink save(GuardianLink guardianLink);

    boolean existsByUser_UserIdAndGuardian_UserId(Long userId, Long guardianId);

    default void validateNotLinked(Long userId, Long guardianId) {
        if (existsByUser_UserIdAndGuardian_UserId(userId, guardianId)) {
            throw new ExceptionList(GuardianLinkErrorCode.ALREADY_LINKED);
        }
    }
}
