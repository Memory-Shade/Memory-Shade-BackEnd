package com.memoryshade.domain.guardianLink.service;

import com.memoryshade.domain.guardianLink.dto.GuardianLinkCreateRequestDto;
import com.memoryshade.domain.guardianLink.dto.GuardianLinkCreateResponseDto;
import com.memoryshade.domain.guardianLink.exception.GuardianLinkErrorCode;
import com.memoryshade.domain.guardianLink.model.GuardianLink;
import com.memoryshade.domain.guardianLink.repository.GuardianLinkRepository;
import com.memoryshade.domain.user.model.Role;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import com.memoryshade.global.exception.ExceptionList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuardianLinkService {

    private final GuardianLinkRepository guardianLinkRepository;
    private final UserRepository userRepository;

    @Transactional
    public GuardianLinkCreateResponseDto createGuardianRelation(
            Long loginUserId,
            Long userId,
            GuardianLinkCreateRequestDto request
    ) {
        if (loginUserId == null) {
            throw new ExceptionList(GuardianLinkErrorCode.UNAUTHORIZED_GUARDIAN);
        }

        User guardian = userRepository.getByUserId(loginUserId);
        if (guardian.getRole() != Role.GUARDIAN) {
            throw new ExceptionList(GuardianLinkErrorCode.GUARDIAN_ONLY);
        }

        User user = userRepository.getByUserId(userId);
        if (user.getRole() != Role.USER) {
            throw new ExceptionList(GuardianLinkErrorCode.TARGET_USER_ONLY);
        }

        if (!user.getPhoneNumber().equals(request.phoneNumber())) {
            throw new ExceptionList(GuardianLinkErrorCode.PHONE_NUMBER_MISMATCH);
        }

        if (guardian.getUserId().equals(user.getUserId())) {
            throw new ExceptionList(GuardianLinkErrorCode.SELF_LINK_NOT_ALLOWED);
        }

        guardianLinkRepository.validateNotLinked(userId, guardian.getUserId());

        GuardianLink guardianLink = guardianLinkRepository.save(
                GuardianLink.builder()
                        .user(user)
                        .guardian(guardian)
                        .build()
        );

        return GuardianLinkCreateResponseDto.fromGuardian(guardianLink);
    }
}
