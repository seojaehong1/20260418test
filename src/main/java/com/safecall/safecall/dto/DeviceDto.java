package com.safecall.safecall.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class DeviceDto {

    @Getter @Setter @NoArgsConstructor
    public static class RegisterRequest {
        private String deviceName;
    }

    @Getter @Setter @NoArgsConstructor
    public static class BatteryUpdateRequest {
        private int batteryLevel;
    }

    @Getter @AllArgsConstructor
    public static class Response {
        private Long id;
        private String deviceCode;
        private String deviceName;
        private int batteryLevel;
        private String status;
        private String ownerName;
    }


}