package com.interviewai.auth;

import com.interviewai.dto.auth.AuthResponse;
import com.interviewai.dto.auth.LoginRequest;
import com.interviewai.dto.auth.RegisterRequest;
import com.interviewai.exception.BadRequestException;
import com.interviewai.exception.UnauthorizedException;
import com.interviewai.mapper.UserMapper;
import com.interviewai.model.RefreshToken;
import com.interviewai.model.User;
import com.interviewai.repository.RefreshTokenRepository;
import com.interviewai.repository.UserRepository;
import com.interviewai.security.JwtService;
import com.interviewai.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
//import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private JwtService jwtService;
    @Mock private UserMapper userMapper;

    private PasswordEncoder passwordEncoder;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder(4);
        authService = new AuthServiceImpl(
                userRepository, refreshTokenRepository, passwordEncoder, jwtService, userMapper);
    }

    @Test
    void register_createsUserAndReturnsTokens() {
        RegisterRequest req = RegisterRequest.builder()
                .name("Jane").email("Jane@Example.com").password("hunter2hunter2").build();

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId("u-1");
            return u;
        });
        when(jwtService.generateAccessToken(anyString(), anyString(), ArgumentMatchers.anySet()))
                .thenReturn("access-token");
        when(jwtService.generateRefreshToken(anyString(), anyString()))
                .thenReturn("refresh-token");
        when(jwtService.getAccessTokenExpirationMs()).thenReturn(900_000L);
        when(jwtService.getRefreshTokenExpirationMs()).thenReturn(604_800_000L);

        AuthResponse response = authService.register(req, "agent", "127.0.0.1");

        assertThat(response.getTokens().getAccessToken()).isEqualTo("access-token");
        assertThat(response.getTokens().getRefreshToken()).isEqualTo("refresh-token");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void register_rejectsDuplicateEmail() {
        when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);
        RegisterRequest req = RegisterRequest.builder()
                .name("Dup").email("dup@example.com").password("hunter2hunter2").build();
        assertThatThrownBy(() -> authService.register(req, null, null))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void login_rejectsWrongPassword() {
        User stored = User.builder()
                .id("u-1").email("u@example.com")
                .passwordHash(passwordEncoder.encode("correct"))
                .roles(Set.of("USER")).status("ACTIVE").build();
        when(userRepository.findByEmail("u@example.com")).thenReturn(Optional.of(stored));

        LoginRequest req = LoginRequest.builder()
                .email("u@example.com").password("wrong").build();

        assertThatThrownBy(() -> authService.login(req, null, null))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void login_succeedsWithCorrectPassword() {
        User stored = User.builder()
                .id("u-1").email("u@example.com")
                .passwordHash(passwordEncoder.encode("correct"))
                .roles(Set.of("USER")).status("ACTIVE").build();
        when(userRepository.findByEmail("u@example.com")).thenReturn(Optional.of(stored));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateAccessToken(anyString(), anyString(), ArgumentMatchers.anySet()))
                .thenReturn("access-token");
        when(jwtService.generateRefreshToken(anyString(), anyString()))
                .thenReturn("refresh-token");
        when(jwtService.getAccessTokenExpirationMs()).thenReturn(900_000L);
        when(jwtService.getRefreshTokenExpirationMs()).thenReturn(604_800_000L);

        LoginRequest req = LoginRequest.builder()
                .email("u@example.com").password("correct").build();

        AuthResponse response = authService.login(req, null, null);

        assertThat(response.getTokens().getAccessToken()).isEqualTo("access-token");
        assertThat(stored.getLastLoginAt()).isNotNull();
    }

    @Test
    void refresh_rejectsExpiredToken() {
        RefreshToken stored = RefreshToken.builder()
                .id("rt-1").userId("u-1").tokenHash("hash")
                .expiresAt(Instant.now().minusSeconds(60))
                .revoked(false).build();
        when(refreshTokenRepository.findByTokenHash(anyString()))
                .thenReturn(Optional.of(stored));

        assertThatThrownBy(() -> authService.refresh("any", null, null))
                .isInstanceOf(UnauthorizedException.class);
    }
}