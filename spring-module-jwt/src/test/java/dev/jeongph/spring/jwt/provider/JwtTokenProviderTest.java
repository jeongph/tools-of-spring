package dev.jeongph.spring.jwt.provider;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import dev.jeongph.spring.jwt.config.JwtProperties;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private JwtProperties properties;

    @BeforeEach
    void setUp() {
        properties = new JwtProperties();
        // 256비트(32바이트) 이상의 시크릿 키
        properties.setSecret(Base64.getEncoder().encodeToString(
                "test-secret-key-for-jwt-token-provider-must-be-long-enough".getBytes()));
        properties.setAccessTokenValidityMs(1_800_000);
        properties.setRefreshTokenValidityMs(604_800_000);

        tokenProvider = new JwtTokenProvider(properties);
    }

    @Test
    @DisplayName("액세스 토큰 생성 및 검증")
    void createAndValidateAccessToken() {
        Authentication authentication = createAuthentication("user@test.com", "ROLE_USER");

        String token = tokenProvider.createAccessToken(authentication);

        assertThat(token).isNotBlank();
        assertThat(tokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("리프레시 토큰 생성 및 검증")
    void createAndValidateRefreshToken() {
        Authentication authentication = createAuthentication("user@test.com", "ROLE_USER");

        String token = tokenProvider.createRefreshToken(authentication);

        assertThat(token).isNotBlank();
        assertThat(tokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("토큰에서 Authentication 추출")
    void getAuthenticationFromToken() {
        Authentication authentication = createAuthentication("user@test.com", "ROLE_USER");
        String token = tokenProvider.createAccessToken(authentication);

        Authentication extracted = tokenProvider.getAuthentication(token);

        assertThat(extracted.getName()).isEqualTo("user@test.com");
        assertThat(extracted.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("잘못된 토큰 검증 실패")
    void validateInvalidToken() {
        assertThat(tokenProvider.validateToken("invalid-token")).isFalse();
    }

    @Test
    @DisplayName("다른 키로 서명된 토큰 검증 실패")
    void validateTokenWithWrongKey() {
        JwtProperties otherProperties = new JwtProperties();
        otherProperties.setSecret(Base64.getEncoder().encodeToString(
                "another-secret-key-for-jwt-token-provider-different-key".getBytes()));
        JwtTokenProvider otherProvider = new JwtTokenProvider(otherProperties);

        Authentication authentication = createAuthentication("user@test.com", "ROLE_USER");
        String token = otherProvider.createAccessToken(authentication);

        assertThat(tokenProvider.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰 검증 실패")
    void validateExpiredToken() {
        JwtProperties expiredProperties = new JwtProperties();
        expiredProperties.setSecret(properties.getSecret());
        expiredProperties.setAccessTokenValidityMs(-1000); // 이미 만료
        JwtTokenProvider expiredProvider = new JwtTokenProvider(expiredProperties);

        Authentication authentication = createAuthentication("user@test.com", "ROLE_USER");
        String token = expiredProvider.createAccessToken(authentication);

        assertThat(tokenProvider.validateToken(token)).isFalse();
    }

    private Authentication createAuthentication(String username, String... roles) {
        List<SimpleGrantedAuthority> authorities = List.of(roles).stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new UsernamePasswordAuthenticationToken(username, "password", authorities);
    }
}
