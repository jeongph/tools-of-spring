package dev.jeongph.spring.example.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}
