package com.interviewai.service.impl;

import com.interviewai.dto.auth.AuthResponse;
import com.interviewai.dto.auth.LoginRequest;
import com.interviewai.dto.auth.RegisterRequest;
import com.interviewai.dto.auth.TokenPair;
import com.interviewai.exception.BadRequestException;
import com.interviewai.exception.UnauthorizedException;
import com.interviewai.mapper.UserMapper;
import com.interviewai.model.RefreshToken;
import com.interviewai.model.User;
import com.interviewai.repository.RefreshTokenRepository;
import com.interviewai.repository.UserRepository;
import com.interviewai.security.JwtService;
import com.interviewai.service.AuthService;
import com.interviewai.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserRepository userRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @Override
    public AuthResponse register(RegisterRequest request, String userAgent, String ipAddress) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already registered");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(Constants.ROLE_USER))
                .plan("FREE")
                .status("ACTIVE")
                .build();
        user = userRepository.save(user);

        TokenPair tokens = issueTokens(user, userAgent, ipAddress);
        log.info("Registered new user {}", user.getId());
        return AuthResponse.builder()
                .user(userMapper.toResponse(user))
                .tokens(tokens)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request, String userAgent, String ipAddress) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new UnauthorizedException("Account is not active");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        TokenPair tokens = issueTokens(user, userAgent, ipAddress);
        return AuthResponse.builder()
                .user(userMapper.toResponse(user))
                .tokens(tokens)
                .build();
    }

    @Override
    public TokenPair refresh(String refreshToken, String userAgent, String ipAddress) {
        String hash = hashToken(refreshToken);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (stored.isRevoked()) {
            throw new UnauthorizedException("Refresh token revoked");
        }
        if (stored.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(stored);
            throw new UnauthorizedException("Refresh token expired");
        }

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new UnauthorizedException("User no longer exists"));

        // Rotate: revoke old, issue new
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        return issueTokens(user, userAgent, ipAddress);
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return;
        String hash = hashToken(refreshToken);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    private TokenPair issueTokens(User user, String userAgent, String ipAddress) {
        String access = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRoles());
        String refresh = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        RefreshToken entity = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(hashToken(refresh))
                .expiresAt(Instant.now().plusMillis(jwtService.getRefreshTokenExpirationMs()))
                .createdAt(Instant.now())
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .revoked(false)
                .build();
        refreshTokenRepository.save(entity);

        return TokenPair.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .accessTokenExpiresInMs(jwtService.getAccessTokenExpirationMs())
                .refreshTokenExpiresInMs(jwtService.getRefreshTokenExpirationMs())
                .tokenType("Bearer")
                .build();
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
