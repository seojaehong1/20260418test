package com.safecall.safecall.repository;

import com.safecall.safecall.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByMemberId(Long memberId);
    Optional<Device> findByDeviceCode(String deviceCode);
}