package com.kh.ypjp.security.model.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginRequest {
        private String email;
        private String password;
        private String username;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthResult {
        private String accessToken;
        private String refreshToken;
        private User user;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class User {
        private Long userNo;
        private String password;
        private String email;
        private String username;
        private Long imageNo;
        private String status;
        private LocalDate inactiveDate;
        private List<String> roles;
        private String provider;
        
        private String profile;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserIdentities {
        private String username;
        private Long userNo;
        private String accessToken;
        private String provider;
        private String providerUserId;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Authority {
        private Long userNo;
        private List<String> roles;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserCredential {
        private Long userNo;
        private String password;
    }
}