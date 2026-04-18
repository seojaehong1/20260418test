package com.safecall.safecall.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class GuardianDto {

    @Getter @Setter @NoArgsConstructor
    public static class RegisterRequest {
        private String guardianEmail;
        private String relationship;
    }

    @Getter @AllArgsConstructor
    public static class Response {
        private Long id;
        private String guardianName;
        private String guardianEmail;
        private String relationship;
        private String ownerName;
    }
}