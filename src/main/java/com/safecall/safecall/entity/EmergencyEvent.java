package com.safecall.safecall.entity;

import com.safecall.safecall.entity.enums.EventStatus;
import com.safecall.safecall.entity.enums.EventType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmergencyEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    private double latitude;

    private double longitude;

    private String address;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    private LocalDateTime occurredAt;

    private LocalDateTime resolvedAt;

    @Builder
    public EmergencyEvent(Device device, double latitude, double longitude,
                          String address, EventType eventType) {
        this.device = device;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.eventType = eventType;
        this.eventStatus = EventStatus.OCCURRED;
        this.occurredAt = LocalDateTime.now();
    }

    public void notify_() {
        this.eventStatus = EventStatus.NOTIFIED;
    }

    public void resolve() {
        this.eventStatus = EventStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
    }
}