package com.safecall.safecall.controller;

import com.safecall.safecall.service.FcmTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    @PostMapping("/token")
    public ResponseEntity<Void> registerToken(
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        String token = body.get("token");
        fcmTokenService.registerToken(memberId, token);
        return ResponseEntity.ok().build();
    }
}