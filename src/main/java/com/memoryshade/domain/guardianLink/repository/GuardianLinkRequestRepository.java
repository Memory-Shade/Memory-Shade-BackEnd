package com.memoryshade.domain.guardianLink.repository;

import com.memoryshade.domain.guardianLink.exception.GuardianLinkErrorCode;
import com.memoryshade.domain.guardianLink.model.GuardianLinkRequest;
import com.memoryshade.domain.guardianLink.model.GuardianLinkRequestStatus;
import com.memoryshade.global.exception.ExceptionList;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface GuardianLinkRequestRepository extends Repository<GuardianLinkRequest, Long> {

    GuardianLinkRequest save(GuardianLinkRequest guardianLinkRequest);

    Optional<GuardianLinkRequest> findByRequestId(Long requestId);

    boolean existsByGuardian_UserIdAndUser_UserIdAndStatus(
            Long guardianId,
            Long userId,
            GuardianLinkRequestStatus status
    );

    List<GuardianLinkRequest> findAllByUser_UserIdAndStatusOrderByCreatedAtDesc(
            Long userId,
            GuardianLinkRequestStatus status
    );

    default GuardianLinkRequest getByRequestId(Long requestId) {
        return findByRequestId(requestId)
                .orElseThrow(() -> new ExceptionList(GuardianLinkErrorCode.GUARDIAN_LINK_REQUEST_NOT_FOUND));
    }

    default void validateNotPending(Long guardianId, Long userId) {
        if (existsByGuardian_UserIdAndUser_UserIdAndStatus(
                guardianId,
                userId,
                GuardianLinkRequestStatus.PENDING
        )) {
            throw new ExceptionList(GuardianLinkErrorCode.ALREADY_REQUESTED);
        }
    }
}
