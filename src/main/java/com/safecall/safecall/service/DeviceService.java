package com.safecall.safecall.service;

import com.safecall.safecall.dto.DeviceDto;
import com.safecall.safecall.entity.Device;
import com.safecall.safecall.entity.Member;
import com.safecall.safecall.repository.DeviceRepository;
import com.safecall.safecall.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final MemberRepository memberRepository;

    // 기기 등록
    public DeviceDto.Response register(Long memberId, DeviceDto.RegisterRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        String deviceCode = "SC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Device device = Device.builder()
                .member(member)
                .deviceCode(deviceCode)
                .deviceName(request.getDeviceName())
                .build();

        deviceRepository.save(device);
        log.info("기기 등록 완료: {} ({})", deviceCode, member.getName());

        return toResponse(device);
    }

    // 내 기기 목록 조회
    @Transactional(readOnly = true)
    public List<DeviceDto.Response> getMyDevices(Long memberId) {
        return deviceRepository.findByMemberId(memberId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 배터리 상태 업데이트
    public void updateBattery(String deviceCode, int batteryLevel) {
        Device device = deviceRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new RuntimeException("기기를 찾을 수 없습니다."));

        device.updateBattery(batteryLevel);
        log.info("배터리 업데이트: {} → {}%", deviceCode, batteryLevel);
    }

    // 기기 삭제
    public void deleteDevice(Long deviceId, Long memberId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("기기를 찾을 수 없습니다."));

        if (!device.getMember().getId().equals(memberId)) {
            throw new RuntimeException("본인의 기기만 삭제할 수 있습니다.");
        }

        deviceRepository.delete(device);
        log.info("기기 삭제: {}", device.getDeviceCode());
    }

    private DeviceDto.Response toResponse(Device device) {
        return new DeviceDto.Response(
                device.getId(),
                device.getDeviceCode(),
                device.getDeviceName(),
                device.getBatteryLevel(),
                device.getStatus().name(),
                device.getMember().getName()
        );
    }

    // 전체 기기 목록 조회 (ADMIN 전용)
    @Transactional(readOnly = true)
    public List<DeviceDto.Response> getAllDevices() {
        return deviceRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}