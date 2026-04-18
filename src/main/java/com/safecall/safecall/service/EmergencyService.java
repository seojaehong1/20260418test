package com.safecall.safecall.service;

import com.safecall.safecall.dto.EmergencyDto;
import com.safecall.safecall.entity.Device;
import com.safecall.safecall.entity.EmergencyEvent;
import com.safecall.safecall.entity.Guardian;
import com.safecall.safecall.entity.Member;
import com.safecall.safecall.entity.enums.DeviceStatus;
import com.safecall.safecall.entity.enums.EventType;
import com.safecall.safecall.repository.DeviceRepository;
import com.safecall.safecall.repository.EmergencyEventRepository;
import com.safecall.safecall.repository.GuardianRepository;
import com.safecall.safecall.service.FcmPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmergencyService {

    private final EmergencyEventRepository emergencyEventRepository;
    private final DeviceRepository deviceRepository;
    private final GuardianRepository guardianRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final FcmPushService fcmPushService;

    // 응급 알림 발생
    public EmergencyDto.Response triggerEmergency(EmergencyDto.Request request) {
        // 1. 기기 조회
        Device device = deviceRepository.findByDeviceCode(request.getDeviceCode())
                .orElseThrow(() -> new RuntimeException("기기를 찾을 수 없습니다."));

        // 2. 기기 상태를 EMERGENCY로 변경
        device.updateStatus(DeviceStatus.EMERGENCY);

        // 3. 이벤트 저장
        EmergencyEvent event = EmergencyEvent.builder()
                .device(device)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .eventType(EventType.valueOf(request.getEventType()))
                .build();

        emergencyEventRepository.save(event);

        // 4. 보호자들에게 WebSocket 알림 발송
        Member owner = device.getMember();
        EmergencyDto.AlertMessage alert = new EmergencyDto.AlertMessage(
                event.getId(),
                owner.getName(),
                owner.getPhone(),
                device.getDeviceName(),
                request.getLatitude(),
                request.getLongitude(),
                request.getAddress(),
                request.getEventType(),
                owner.getName() + "님의 응급 상황이 발생했습니다!",
                event.getOccurredAt()
        );

        List<Guardian> guardians = guardianRepository.findByMemberId(owner.getId());
        for (Guardian guardian : guardians) {
            Long guardianMemberId = guardian.getGuardianMember().getId();
            messagingTemplate.convertAndSend(
                    "/topic/alerts." + guardianMemberId, alert);
            log.info("응급 알림 전송: {} → 보호자 {}",
                    owner.getName(), guardian.getGuardianMember().getName());
            fcmPushService.sendToMember(
                    guardianMemberId,
                    "🚨 응급 알림",
                    owner.getName() + "님의 응급 상황이 발생했습니다!"
            );
        }

        messagingTemplate.convertAndSend(
                "/topic/emergency/" + device.getDeviceCode(), alert);
        messagingTemplate.convertAndSend("/topic/admin/emergencies", alert);

        // 5. 이벤트 상태를 NOTIFIED로 변경
        event.notify_();

        log.info("응급 이벤트 발생: {} / 기기: {} / 위치: {}, {}",
                owner.getName(), device.getDeviceCode(),
                request.getLatitude(), request.getLongitude());

        return toResponse(event);
    }

    // 응급 상황 해제
    public void resolveEmergency(Long eventId) {
        EmergencyEvent event = emergencyEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("이벤트를 찾을 수 없습니다."));

        event.resolve();
        event.getDevice().updateStatus(DeviceStatus.ACTIVE);

        // 보호자에게 해제 알림
        Member owner = event.getDevice().getMember();
        List<Guardian> guardians = guardianRepository.findByMemberId(owner.getId());
        for (Guardian guardian : guardians) {
            Long guardianMemberId = guardian.getGuardianMember().getId();
            messagingTemplate.convertAndSend(
                    "/topic/alerts." + guardianMemberId,
                    new EmergencyDto.AlertMessage(
                            event.getId(), owner.getName(), owner.getPhone(),
                            event.getDevice().getDeviceName(),
                            event.getLatitude(), event.getLongitude(),
                            event.getAddress(), "RESOLVED",
                            owner.getName() + "님의 응급 상황이 해제되었습니다.",
                            LocalDateTime.now()
                    ));
        }

        log.info("응급 해제: 이벤트 {}", eventId);
    }

    // 내 응급 이벤트 이력 조회
    @Transactional(readOnly = true)
    public List<EmergencyDto.Response> getMyEvents(Long memberId) {
        return emergencyEventRepository.findByDeviceMemberIdOrderByOccurredAtDesc(memberId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private EmergencyDto.Response toResponse(EmergencyEvent event) {
        Device device = event.getDevice();
        Member owner = device.getMember();
        return new EmergencyDto.Response(
                event.getId(),
                device.getDeviceCode(),
                device.getDeviceName(),
                owner.getName(),
                owner.getPhone(),
                event.getLatitude(),
                event.getLongitude(),
                event.getAddress(),
                event.getEventType().name(),
                event.getEventStatus().name(),
                event.getOccurredAt()
        );
    }
}