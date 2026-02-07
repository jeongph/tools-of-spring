package dev.jeongph.spring.jwt.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import dev.jeongph.spring.jwt.filter.JwtAuthenticationFilter;
import dev.jeongph.spring.jwt.handler.JwtAccessDeniedHandler;
import dev.jeongph.spring.jwt.handler.JwtAuthenticationEntryPoint;
import dev.jeongph.spring.jwt.provider.JwtTokenProvider;
import io.jsonwebtoken.Jwts;

@AutoConfiguration
@ConditionalOnClass({EnableWebSecurity.class, Jwts.class})
@ConditionalOnProperty(prefix = "jwt", name = "secret")
@EnableConfigurationProperties(JwtProperties.class)
public class JwtAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JwtTokenProvider jwtTokenProvider(JwtProperties properties) {
        return new JwtTokenProvider(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider tokenProvider,
                                                           JwtProperties properties) {
        return new JwtAuthenticationFilter(tokenProvider, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtAccessDeniedHandler jwtAccessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }
}
