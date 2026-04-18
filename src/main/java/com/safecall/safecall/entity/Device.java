package com.safecall.safecall.entity;

import com.safecall.safecall.entity.enums.DeviceStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(unique = true, nullable = false)
    private String deviceCode;

    private String deviceName;

    private int batteryLevel;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    private LocalDateTime registeredAt;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<EmergencyEvent> emergencyEvents = new ArrayList<>();

    @Builder
    public Device(Member member, String deviceCode, String deviceName) {
        this.member = member;
        this.deviceCode = deviceCode;
        this.deviceName = deviceName;
        this.batteryLevel = 100;
        this.status = DeviceStatus.ACTIVE;
        this.registeredAt = LocalDateTime.now();
    }

    public void updateBattery(int level) {
        this.batteryLevel = level;
    }

    public void updateStatus(DeviceStatus status) {
        this.status = status;
    }
}