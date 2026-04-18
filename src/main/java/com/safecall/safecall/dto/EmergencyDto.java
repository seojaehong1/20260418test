package com.safecall.safecall.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class EmergencyDto {

    @Getter @Setter @NoArgsConstructor
    public static class Request {
        private String deviceCode;
        private double latitude;
        private double longitude;
        private String address;
        private String eventType;  // BUTTON_PRESS, FALL_DETECT
    }

    @Getter @AllArgsConstructor
    public static class Response {
        private Long id;
        private String deviceCode;
        private String deviceName;
        private String ownerName;
        private String ownerPhone;
        private double latitude;
        private double longitude;
        private String address;
        private String eventType;
        private String eventStatus;
        private LocalDateTime occurredAt;
    }

    @Getter @AllArgsConstructor
    public static class AlertMessage {
        private Long eventId;
        private String ownerName;
        private String ownerPhone;
        private String deviceName;
        private double latitude;
        private double longitude;
        private String address;
        private String eventType;
        private String message;
        private LocalDateTime occurredAt;
    }
}