package com.memoryshade.domain.guardianLink.service;


import com.memoryshade.domain.guardianLink.dto.*;
import com.memoryshade.domain.guardianLink.exception.GuardianLinkErrorCode;
import com.memoryshade.domain.guardianLink.model.GuardianLink;
import com.memoryshade.domain.guardianLink.model.GuardianLinkRequest;
import com.memoryshade.domain.guardianLink.model.GuardianLinkRequestStatus;
import com.memoryshade.domain.guardianLink.repository.GuardianLinkRepository;
import com.memoryshade.domain.guardianLink.repository.GuardianLinkRequestRepository;
import com.memoryshade.domain.notification.service.NotificationService;
import com.memoryshade.domain.user.model.Role;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import com.memoryshade.global.exception.ExceptionList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuardianLinkRequestService {

    private final GuardianLinkRepository guardianLinkRepository;
    private final GuardianLinkRequestRepository guardianLinkRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public GuardianLinkRequestCreateResponseDto createGuardianLinkRequest(
            Long loginUserId,
            GuardianLinkCreateRequestDto request
    ) {
        if (loginUserId == null) {
            throw new ExceptionList(GuardianLinkErrorCode.UNAUTHORIZED_GUARDIAN);
        }

        User guardian = userRepository.getByUserId(loginUserId);
        if (guardian.getRole() != Role.GUARDIAN) {
            throw new ExceptionList(GuardianLinkErrorCode.GUARDIAN_ONLY);
        }

        User user = userRepository.getByPhoneNumber(request.phoneNumber());
        if (user.getRole() != Role.USER) {
            throw new ExceptionList(GuardianLinkErrorCode.TARGET_USER_ONLY);
        }

        if (guardian.getUserId().equals(user.getUserId())) {
            throw new ExceptionList(GuardianLinkErrorCode.SELF_LINK_NOT_ALLOWED);
        }

        guardianLinkRepository.validateNotLinked(user.getUserId(), guardian.getUserId());
        guardianLinkRequestRepository.validateNotPending(guardian.getUserId(), user.getUserId());

        GuardianLinkRequest guardianLinkRequest = guardianLinkRequestRepository.save(
                GuardianLinkRequest.builder()
                        .user(user)
                        .guardian(guardian)
                        .build()
        );

        notificationService.createGuardianLinkRequestNotification(
                user.getUserId(),
                guardian.getName(),
                guardianLinkRequest.getRequestId()
        );

        return GuardianLinkRequestCreateResponseDto.fromGuardianLinkRequest(guardianLinkRequest);
    }

    public List<GuardianLinkRequestGetResponseDto> getMyGuardianLinkRequests(Long loginUserId) {
        if (loginUserId == null) {
            throw new ExceptionList(GuardianLinkErrorCode.UNAUTHORIZED_USER);
        }

        User user = userRepository.getByUserId(loginUserId);
        if (user.getRole() != Role.USER) {
            throw new ExceptionList(GuardianLinkErrorCode.USER_ONLY);
        }

        return guardianLinkRequestRepository
                .findAllByUser_UserIdAndStatusOrderByCreatedAtDesc(loginUserId, GuardianLinkRequestStatus.PENDING)
                .stream()
                .map(GuardianLinkRequestGetResponseDto::fromGuardianLinkRequest)
                .toList();
    }

    @Transactional
    public GuardianLinkCreateResponseDto acceptGuardianLinkRequest(Long loginUserId, Long requestId) {
        if (loginUserId == null) {
            throw new ExceptionList(GuardianLinkErrorCode.UNAUTHORIZED_USER);
        }

        User user = userRepository.getByUserId(loginUserId);
        if (user.getRole() != Role.USER) {
            throw new ExceptionList(GuardianLinkErrorCode.USER_ONLY);
        }

        GuardianLinkRequest request = guardianLinkRequestRepository.getByRequestId(requestId);

        if (!request.getUser().getUserId().equals(loginUserId)) {
            throw new ExceptionList(GuardianLinkErrorCode.GUARDIAN_LINK_REQUEST_NOT_FOUND);
        }

        if (request.getStatus() != GuardianLinkRequestStatus.PENDING) {
            throw new ExceptionList(GuardianLinkErrorCode.REQUEST_ALREADY_PROCESSED);
        }

        guardianLinkRepository.validateNotLinked(
                request.getUser().getUserId(),
                request.getGuardian().getUserId()
        );

        GuardianLink guardianLink = guardianLinkRepository.save(
                GuardianLink.builder()
                        .user(request.getUser())
                        .guardian(request.getGuardian())
                        .build()
        );

        request.accept();

        return GuardianLinkCreateResponseDto.fromGuardian(guardianLink);
    }

    @Transactional
    public GuardianLinkRequestRejectResponseDto rejectGuardianLinkRequest(Long loginUserId, Long requestId) {
        if (loginUserId == null) {
            throw new ExceptionList(GuardianLinkErrorCode.UNAUTHORIZED_USER);
        }

        User user = userRepository.getByUserId(loginUserId);
        if (user.getRole() != Role.USER) {
            throw new ExceptionList(GuardianLinkErrorCode.USER_ONLY);
        }

        GuardianLinkRequest request = guardianLinkRequestRepository.getByRequestId(requestId);

        if (!request.getUser().getUserId().equals(loginUserId)) {
            throw new ExceptionList(GuardianLinkErrorCode.GUARDIAN_LINK_REQUEST_NOT_FOUND);
        }

        if (request.getStatus() != GuardianLinkRequestStatus.PENDING) {
            throw new ExceptionList(GuardianLinkErrorCode.REQUEST_ALREADY_PROCESSED);
        }

        request.reject();

        return GuardianLinkRequestRejectResponseDto.from(
                request.getRequestId(),
                request.getStatus()
        );
    }
}
