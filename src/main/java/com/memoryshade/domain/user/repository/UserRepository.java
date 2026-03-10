package com.memoryshade.domain.user.repository;

import com.memoryshade.domain.auth.exception.AuthErrorCode;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.global.exception.ExceptionList;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {

    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByUserId(Long userId);

    User save(User user);

    default User getByPhoneNumber(String phoneNumber) {
        return findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ExceptionList(AuthErrorCode.USER_NOT_FOUND));
    }

    default User getByUserId(Long userId) {
        return findByUserId(userId)
                .orElseThrow(() -> new ExceptionList(AuthErrorCode.USER_NOT_FOUND));
    }

    default void validateDuplicatePhoneNumber(String phoneNumber) {
        findByPhoneNumber(phoneNumber)
                .ifPresent(u -> { throw new ExceptionList(AuthErrorCode.DUPLICATE_PHONE_NUMBER); });
    }
}
