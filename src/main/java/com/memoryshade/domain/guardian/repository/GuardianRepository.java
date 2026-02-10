package com.memoryshade.domain.guardian.repository;

import com.memoryshade.domain.guardian.model.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Integer> {
}
