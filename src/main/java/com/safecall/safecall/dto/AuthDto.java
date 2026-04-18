package com.safecall.safecall.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AuthDto {

    @Getter @Setter @NoArgsConstructor
    public static class SignUpRequest {
        private String email;
        private String password;
        private String name;
        private String phone;
        private String role;  // USER, GUARDIAN
    }

    @Getter @Setter @NoArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Getter @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
        private String role;
        private String name;
    }
}