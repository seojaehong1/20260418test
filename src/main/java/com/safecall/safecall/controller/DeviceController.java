package com.safecall.safecall.controller;

import com.safecall.safecall.dto.DeviceDto;
import com.safecall.safecall.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.safecall.safecall.dto.HeartbeatRequest;
import com.safecall.safecall.device.dto.DeviceStatus;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;
    private final com.safecall.safecall.device.service.DeviceStatusService deviceStatusService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/heartbeat")
    public ResponseEntity<DeviceStatus> heartbeat(@RequestBody HeartbeatRequest request) {
        DeviceStatus status = deviceStatusService.updateHeartbeat(
                request.getDeviceCode(),
                request.getBatteryLevel(),
                request.getLatitude(),
                request.getLongitude()
        );

        // 해당 기기 구독자에게 실시간 브로드캐스트
        messagingTemplate.convertAndSend("/topic/device/" + request.getDeviceCode(), status);

        return ResponseEntity.ok(status);
    }

    /**
     * 기기 현재 상태 조회 (사용자/보호자 → 서버)
     */
    @GetMapping("/{deviceCode}/status")
    public ResponseEntity<com.safecall.safecall.device.dto.DeviceStatus> getStatus(@PathVariable String deviceCode) {
        return ResponseEntity.ok(deviceStatusService.getStatus(deviceCode));
    }

    // 기기 등록
    @PostMapping
    public ResponseEntity<DeviceDto.Response> register(
            Authentication authentication,
            @RequestBody DeviceDto.RegisterRequest request) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(deviceService.register(memberId, request));
    }

    // 내 기기 목록
    @GetMapping
    public ResponseEntity<List<DeviceDto.Response>> getMyDevices(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(deviceService.getMyDevices(memberId));
    }

    // 배터리 업데이트
    @PatchMapping("/{deviceCode}/battery")
    public ResponseEntity<String> updateBattery(
            @PathVariable String deviceCode,
            @RequestBody DeviceDto.BatteryUpdateRequest request) {
        deviceService.updateBattery(deviceCode, request.getBatteryLevel());
        return ResponseEntity.ok("배터리 업데이트 완료");
    }

    // 기기 삭제
    @DeleteMapping("/{deviceId}")
    public ResponseEntity<String> deleteDevice(
            @PathVariable Long deviceId,
            Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        deviceService.deleteDevice(deviceId, memberId);
        return ResponseEntity.ok("기기 삭제 완료");
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DeviceDto.Response>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }





}