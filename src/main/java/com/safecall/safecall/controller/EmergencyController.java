package com.safecall.safecall.controller;

import com.safecall.safecall.dto.EmergencyDto;
import com.safecall.safecall.service.EmergencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emergency")
public class EmergencyController {

    private final EmergencyService emergencyService;

    // 응급 알림 발생
    @PostMapping
    public ResponseEntity<EmergencyDto.Response> trigger(
            @RequestBody EmergencyDto.Request request) {
        return ResponseEntity.ok(emergencyService.triggerEmergency(request));
    }

    // 응급 상황 해제
    @PatchMapping("/{eventId}/resolve")
    public ResponseEntity<String> resolve(@PathVariable Long eventId) {
        emergencyService.resolveEmergency(eventId);
        return ResponseEntity.ok("응급 상황 해제 완료");
    }

    // 내 응급 이벤트 이력
    @GetMapping("/history")
    public ResponseEntity<List<EmergencyDto.Response>> getMyEvents(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(emergencyService.getMyEvents(memberId));
    }
}