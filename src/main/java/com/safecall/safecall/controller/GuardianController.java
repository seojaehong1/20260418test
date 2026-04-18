package com.safecall.safecall.controller;

import com.safecall.safecall.dto.GuardianDto;
import com.safecall.safecall.service.GuardianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guardians")
public class GuardianController {

    private final GuardianService guardianService;

    // 보호자 등록
    @PostMapping
    public ResponseEntity<GuardianDto.Response> register(
            Authentication authentication,
            @RequestBody GuardianDto.RegisterRequest request) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(guardianService.register(memberId, request));
    }

    // 내 보호자 목록
    @GetMapping
    public ResponseEntity<List<GuardianDto.Response>> getMyGuardians(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(guardianService.getMyGuardians(memberId));
    }

    // 내가 보호하는 사용자 목록
    @GetMapping("/protectees")
    public ResponseEntity<List<GuardianDto.Response>> getMyProtectees(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(guardianService.getMyProtectees(memberId));
    }

    // 보호자 삭제
    @DeleteMapping("/{guardianId}")
    public ResponseEntity<String> deleteGuardian(
            @PathVariable Long guardianId,
            Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        guardianService.deleteGuardian(guardianId, memberId);
        return ResponseEntity.ok("보호자 삭제 완료");
    }

    // GuardianController.java
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GuardianDto.Response>> getAllGuardians() {
        return ResponseEntity.ok(guardianService.getAllGuardians());
    }
}