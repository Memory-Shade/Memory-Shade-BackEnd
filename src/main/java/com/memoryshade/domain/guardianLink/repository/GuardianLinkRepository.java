package com.memoryshade.domain.guardianLink.repository;

import com.memoryshade.domain.guardianLink.exception.GuardianLinkErrorCode;
import com.memoryshade.domain.guardianLink.model.GuardianLink;
import com.memoryshade.global.exception.ExceptionList;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface GuardianLinkRepository extends Repository<GuardianLink, Long> {

    GuardianLink save(GuardianLink guardianLink);

    void delete(GuardianLink guardianLink);

    boolean existsByUser_UserIdAndGuardian_UserId(Long userId, Long guardianId);

    Optional<GuardianLink> findByUser_UserIdAndGuardian_UserId(Long userId, Long guardianId);

    default void validateLinked(Long userId, Long guardianId) {
        if (!existsByUser_UserIdAndGuardian_UserId(userId, guardianId)) {
            throw new ExceptionList(GuardianLinkErrorCode.GUARDIAN_LINK_NOT_FOUND);
        }
    }

    default void validateNotLinked(Long userId, Long guardianId) {
        if (existsByUser_UserIdAndGuardian_UserId(userId, guardianId)) {
            throw new ExceptionList(GuardianLinkErrorCode.ALREADY_LINKED);
        }
    }

    default GuardianLink getByUserIdAndGuardianId(Long userId, Long guardianId) {
        return findByUser_UserIdAndGuardian_UserId(userId, guardianId)
                .orElseThrow(() -> new ExceptionList(GuardianLinkErrorCode.GUARDIAN_LINK_NOT_FOUND));
    }

    List<GuardianLink> findAllByGuardian_UserId(Long guardianId);

    List<GuardianLink> findAllByUser_UserId(Long userId);
}
