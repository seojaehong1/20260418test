package com.safecall.safecall.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeartbeatRequest {
    private String deviceCode;
    private Integer batteryLevel;
    private Double latitude;
    private Double longitude;
}