package com.safecall.safecall.service;

import com.safecall.safecall.dto.GuardianDto;
import com.safecall.safecall.entity.Guardian;
import com.safecall.safecall.entity.Member;
import com.safecall.safecall.repository.GuardianRepository;
import com.safecall.safecall.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GuardianService {

    private final GuardianRepository guardianRepository;
    private final MemberRepository memberRepository;

    // 보호자 등록
    public GuardianDto.Response register(Long memberId, GuardianDto.RegisterRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        Member guardianMember = memberRepository.findByEmail(request.getGuardianEmail())
                .orElseThrow(() -> new RuntimeException("보호자 이메일을 찾을 수 없습니다. 보호자도 먼저 회원가입이 필요합니다."));

        if (memberId.equals(guardianMember.getId())) {
            throw new RuntimeException("자기 자신을 보호자로 등록할 수 없습니다.");
        }

        if (guardianRepository.existsByMemberIdAndGuardianMemberId(memberId, guardianMember.getId())) {
            throw new RuntimeException("이미 등록된 보호자입니다.");
        }

        Guardian guardian = Guardian.builder()
                .member(member)
                .guardianMember(guardianMember)
                .relationship(request.getRelationship())
                .build();

        guardianRepository.save(guardian);
        log.info("보호자 등록: {} → {} ({})",
                member.getName(), guardianMember.getName(), request.getRelationship());

        return toResponse(guardian);
    }

    // 내 보호자 목록
    @Transactional(readOnly = true)
    public List<GuardianDto.Response> getMyGuardians(Long memberId) {
        return guardianRepository.findByMemberId(memberId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GuardianDto.Response> getMyProtectees(Long guardianMemberId) {
        return guardianRepository.findByGuardianMemberId(guardianMemberId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 보호자 삭제
    public void deleteGuardian(Long guardianId, Long memberId) {
        Guardian guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new RuntimeException("보호자 관계를 찾을 수 없습니다."));

        if (!guardian.getMember().getId().equals(memberId)) {
            throw new RuntimeException("본인의 보호자만 삭제할 수 있습니다.");
        }

        guardianRepository.delete(guardian);
        log.info("보호자 삭제: {} → {}",
                guardian.getMember().getName(), guardian.getGuardianMember().getName());
    }

    private GuardianDto.Response toResponse(Guardian guardian) {
        return new GuardianDto.Response(
                guardian.getId(),
                guardian.getGuardianMember().getName(),
                guardian.getGuardianMember().getEmail(),
                guardian.getRelationship(),
                guardian.getMember().getName()
        );
    }

    // GuardianService.java
    @Transactional(readOnly = true)
    public List<GuardianDto.Response> getAllGuardians() {
        return guardianRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}