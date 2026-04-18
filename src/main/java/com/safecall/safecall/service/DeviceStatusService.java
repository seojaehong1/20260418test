package com.safecall.safecall.device.service;

import com.safecall.safecall.device.dto.DeviceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeviceStatusService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 키 네임스페이스: device:status:{deviceCode}
    private static final String KEY_PREFIX = "device:status:";
    // 5분 통신 없으면 오프라인으로 간주
    private static final Duration TTL = Duration.ofMinutes(5);

    private String key(String deviceCode) {
        return KEY_PREFIX + deviceCode;
    }

    /**
     * Heartbeat 수신 시 상태 갱신
     */
    public DeviceStatus updateHeartbeat(String deviceCode, Integer batteryLevel,
                                        Double latitude, Double longitude) {
        DeviceStatus status = DeviceStatus.builder()
                .deviceCode(deviceCode)
                .batteryLevel(batteryLevel)
                .online(true)
                .lastHeartbeat(LocalDateTime.now())
                .latitude(latitude)
                .longitude(longitude)
                .build();

        redisTemplate.opsForValue().set(key(deviceCode), status, TTL);
        return status;
    }

    /**
     * 현재 상태 조회. TTL 만료됐으면 null 반환 → 오프라인
     */
    public DeviceStatus getStatus(String deviceCode) {
        Object obj = redisTemplate.opsForValue().get(key(deviceCode));
        if (obj == null) {
            // 캐시 만료 = 오프라인
            return DeviceStatus.builder()
                    .deviceCode(deviceCode)
                    .online(false)
                    .build();
        }
        return (DeviceStatus) obj;
    }

    /**
     * 강제로 오프라인 처리 (테스트/관리용)
     */
    public void markOffline(String deviceCode) {
        redisTemplate.delete(key(deviceCode));
    }
}