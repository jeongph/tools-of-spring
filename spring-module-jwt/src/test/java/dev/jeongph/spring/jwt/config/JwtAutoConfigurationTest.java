package dev.jeongph.spring.jwt.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import dev.jeongph.spring.jwt.filter.JwtAuthenticationFilter;
import dev.jeongph.spring.jwt.handler.JwtAccessDeniedHandler;
import dev.jeongph.spring.jwt.handler.JwtAuthenticationEntryPoint;
import dev.jeongph.spring.jwt.provider.JwtTokenProvider;

class JwtAutoConfigurationTest {

    private static final String SECRET = Base64.getEncoder().encodeToString(
            "test-secret-key-for-jwt-auto-configuration-test-must-be-long".getBytes());

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JwtAutoConfiguration.class));

    @Test
    @DisplayName("jwt.secret 설정 시 모든 빈이 등록됨")
    void allBeansRegisteredWhenSecretConfigured() {
        contextRunner
                .withPropertyValues("jwt.secret=" + SECRET)
                .run(context -> {
                    assertThat(context).hasSingleBean(JwtTokenProvider.class);
                    assertThat(context).hasSingleBean(JwtAuthenticationFilter.class);
                    assertThat(context).hasSingleBean(JwtAuthenticationEntryPoint.class);
                    assertThat(context).hasSingleBean(JwtAccessDeniedHandler.class);
                    assertThat(context).hasSingleBean(JwtProperties.class);
                });
    }

    @Test
    @DisplayName("jwt.secret 미설정 시 빈 등록 안됨")
    void noBeansWithoutSecret() {
        contextRunner
                .run(context -> {
                    assertThat(context).doesNotHaveBean(JwtTokenProvider.class);
                    assertThat(context).doesNotHaveBean(JwtAuthenticationFilter.class);
                });
    }

    @Test
    @DisplayName("사용자 정의 빈이 있으면 자동 설정 빈이 등록되지 않음")
    void userDefinedBeanTakesPrecedence() {
        contextRunner
                .withPropertyValues("jwt.secret=" + SECRET)
                .withBean(JwtTokenProvider.class, () -> {
                    JwtProperties props = new JwtProperties();
                    props.setSecret(SECRET);
                    return new JwtTokenProvider(props);
                })
                .run(context -> assertThat(context).hasSingleBean(JwtTokenProvider.class));
    }
}
