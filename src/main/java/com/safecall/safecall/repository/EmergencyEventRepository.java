package com.safecall.safecall.repository;

import com.safecall.safecall.entity.EmergencyEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmergencyEventRepository extends JpaRepository<EmergencyEvent, Long> {
    List<EmergencyEvent> findByDeviceIdOrderByOccurredAtDesc(Long deviceId);
    List<EmergencyEvent> findByDeviceMemberIdOrderByOccurredAtDesc(Long memberId);
}