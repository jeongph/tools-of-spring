package dev.jeongph.spring.example.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.jeongph.spring.example.auth.dto.LoginRequest;
import dev.jeongph.spring.example.auth.dto.SignupRequest;
import dev.jeongph.spring.example.auth.dto.TokenResponse;
import dev.jeongph.spring.example.user.User;
import dev.jeongph.spring.example.user.UserRepository;
import dev.jeongph.spring.jwt.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다: " + request.email());
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(User.Role.ROLE_USER)
                .build();

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null,
                AuthorityUtils.createAuthorityList(user.getRole().name()));

        return new TokenResponse(
                jwtTokenProvider.createAccessToken(authentication),
                jwtTokenProvider.createRefreshToken(authentication));
    }
}
