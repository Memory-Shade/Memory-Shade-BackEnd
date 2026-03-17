package com.memoryshade.domain.guardianLink.service;

import com.memoryshade.domain.guardianLink.dto.GuardianLinkCreateRequestDto;
import com.memoryshade.domain.guardianLink.dto.GuardianLinkCreateResponseDto;
import com.memoryshade.domain.guardianLink.dto.GuardianLinkGetResponseDto;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuardianLinkService {

    private final GuardianLinkRepository guardianLinkRepository;
    private final UserRepository userRepository;

    @Transactional
    public GuardianLinkCreateResponseDto createGuardianLink(
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

        GuardianLink guardianLink = guardianLinkRepository.save(request.toGuardianLink(user, guardian));

        return GuardianLinkCreateResponseDto.fromGuardian(guardianLink);
    }

    @Transactional(readOnly = true)
    public List<GuardianLinkGetResponseDto> getAllLinkUser(Long loginUserId) {
        if (loginUserId == null) {
            throw new ExceptionList(GuardianLinkErrorCode.UNAUTHORIZED_GUARDIAN);
        }

        User guardian = userRepository.getByUserId(loginUserId);
        if (guardian.getRole() != Role.GUARDIAN) {
            throw new ExceptionList(GuardianLinkErrorCode.GUARDIAN_ONLY);
        }

        return guardianLinkRepository.findAllByGuardian_UserId(loginUserId).stream()
                .map(GuardianLink::getUser)
                .map(GuardianLinkGetResponseDto::fromUser)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GuardianLinkGetResponseDto> getAllLinkGuardian(Long loginUserId) {
        if (loginUserId == null) {
            throw new ExceptionList(GuardianLinkErrorCode.UNAUTHORIZED_USER);
        }

        User user = userRepository.getByUserId(loginUserId);
        if (user.getRole() != Role.USER) {
            throw new ExceptionList(GuardianLinkErrorCode.USER_ONLY);
        }

        return guardianLinkRepository.findAllByUser_UserId(loginUserId).stream()
                .map(GuardianLink::getGuardian)
                .map(GuardianLinkGetResponseDto::fromUser)
                .toList();
    }
}
