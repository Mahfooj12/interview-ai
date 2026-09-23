package com.interviewai.controller;

import com.interviewai.dto.auth.AuthResponse;
import com.interviewai.dto.auth.LoginRequest;
import com.interviewai.dto.auth.RefreshTokenRequest;
import com.interviewai.dto.auth.RegisterRequest;
import com.interviewai.dto.auth.TokenPair;
import com.interviewai.dto.auth.UserResponse;
import com.interviewai.exception.ApiResponse;
import com.interviewai.security.CustomUserDetails;
import com.interviewai.service.AuthService;
import com.interviewai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register, login, refresh, logout")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest http) {
        AuthResponse response = authService.register(request, http.getHeader("User-Agent"), clientIp(http));
        return ResponseEntity.ok(ApiResponse.ok(response, "Registration successful"));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate a user")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest http) {
        AuthResponse response = authService.login(request, http.getHeader("User-Agent"), clientIp(http));
        return ResponseEntity.ok(ApiResponse.ok(response, "Login successful"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Exchange a refresh token for a new access token")
    public ResponseEntity<ApiResponse<TokenPair>> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest http) {
        TokenPair tokens = authService.refresh(request.getRefreshToken(),
                http.getHeader("User-Agent"), clientIp(http));
        return ResponseEntity.ok(ApiResponse.ok(tokens, "Token refreshed"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke a refresh token")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.ok(null, "Logged out"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get the currently authenticated user")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal CustomUserDetails principal) {
        UserResponse profile = userService.getProfile(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    private String clientIp(HttpServletRequest request) {
        String header = request.getHeader("X-Forwarded-For");
        if (header != null && !header.isBlank()) {
            return header.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}