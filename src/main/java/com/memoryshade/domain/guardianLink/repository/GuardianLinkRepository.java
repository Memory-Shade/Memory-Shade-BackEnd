package com.memoryshade.domain.guardianLink.repository;

import com.memoryshade.domain.guardianLink.model.GuardianLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuardianLinkRepository extends JpaRepository<GuardianLink, Integer> {
}
