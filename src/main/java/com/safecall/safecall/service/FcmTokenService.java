package com.safecall.safecall.service;

import com.safecall.safecall.entity.FcmToken;
import com.safecall.safecall.entity.Member;
import com.safecall.safecall.repository.FcmTokenRepository;
import com.safecall.safecall.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void registerToken(Long memberId, String token) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Optional<FcmToken> existing = fcmTokenRepository.findByToken(token);
        if (existing.isPresent()) {
            // 이미 등록된 토큰 → 갱신
            existing.get().updateToken(token);
            log.info("FCM 토큰 갱신: {}", member.getName());
        } else {
            FcmToken fcmToken = FcmToken.builder()
                    .member(member)
                    .token(token)
                    .build();
            fcmTokenRepository.save(fcmToken);
            log.info("FCM 토큰 등록: {} → {}", member.getName(), token.substring(0, 20) + "...");
        }
    }
}