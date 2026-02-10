package com.memoryshade.domain.user.repository;

import com.memoryshade.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
