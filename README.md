# Tools of Spring

> Spring 개발 시 가져다 쓸 유용한 코드, 설계, 모듈

## 프로젝트 구조

```
tools-of-spring/
├── src/                    (예제 애플리케이션 - Spring Boot)
│   └── .../example/
└── spring-module-jwt/      (JWT 인증 라이브러리 - plain JAR)
    └── .../jwt/
```

- 루트 프로젝트가 예제 애플리케이션 역할을 겸한다 (H2 + JPA)
- 서브모듈은 독립 라이브러리로, 다른 프로젝트에서 의존성으로 가져다 쓸 수 있다

## 모듈 네이밍 컨벤션

`{범위}-{타입}-{모듈명}` (예: `spring-module-jwt`)

## spring-module-jwt

JWT 토큰 기반 인증을 위한 Spring Boot 자동 설정 라이브러리.

### 제공 컴포넌트

- `JwtTokenProvider` — 토큰 생성/검증/Authentication 추출
- `JwtAuthenticationFilter` — Bearer 토큰 파싱 필터
- `JwtAuthenticationEntryPoint` — 401 응답 처리
- `JwtAccessDeniedHandler` — 403 응답 처리
- `JwtAutoConfiguration` — 위 컴포넌트 자동 빈 등록

### 사용법

1. 의존성 추가:
```groovy
implementation project(':spring-module-jwt')
```

2. `application.yml` 설정:
```yaml
jwt:
  secret: <Base64 인코딩된 비밀키>
  access-token-validity-ms: 1800000   # 30분 (기본값)
  refresh-token-validity-ms: 604800000 # 7일 (기본값)
```

3. `SecurityFilterChain`에서 필터 등록:
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler))
        .build();
}
```

### 설정 프로퍼티

| 프로퍼티 | 기본값 | 설명 |
|----------|--------|------|
| `jwt.secret` | (필수) | Base64 인코딩된 HMAC-SHA 비밀키 |
| `jwt.access-token-validity-ms` | `1800000` | 액세스 토큰 유효 시간 (ms) |
| `jwt.refresh-token-validity-ms` | `604800000` | 리프레시 토큰 유효 시간 (ms) |
| `jwt.header-name` | `Authorization` | 토큰 헤더 이름 |
| `jwt.token-prefix` | `Bearer ` | 토큰 접두사 |

## 빌드

```bash
./gradlew build
```

## 기술 스택

- Java 21
- Spring Boot 3.3.5
- jjwt 0.12.6
- Gradle 멀티모듈
