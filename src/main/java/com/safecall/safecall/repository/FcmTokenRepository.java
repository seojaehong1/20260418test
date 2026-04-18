package com.safecall.safecall.repository;

import com.safecall.safecall.entity.FcmToken;
import com.safecall.safecall.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByToken(String token);

    List<FcmToken> findByMember(Member member);

    List<FcmToken> findByMemberId(Long memberId);
}