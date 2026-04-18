package com.safecall.safecall.repository;

import com.safecall.safecall.entity.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {
    List<Guardian> findByMemberId(Long memberId);
    List<Guardian> findByGuardianMemberId(Long guardianMemberId);
    boolean existsByMemberIdAndGuardianMemberId(Long memberId, Long guardianMemberId);
}