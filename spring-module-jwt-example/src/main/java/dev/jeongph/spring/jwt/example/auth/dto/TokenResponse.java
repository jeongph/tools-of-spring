package dev.jeongph.spring.jwt.example.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}
