package dev.jeongph.spring.jwt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;

    private long accessTokenValidityMs = 1_800_000; // 30분

    private long refreshTokenValidityMs = 604_800_000; // 7일

    private String headerName = "Authorization";

    private String tokenPrefix = "Bearer ";
}
