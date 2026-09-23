package com.interviewai.service;

import com.interviewai.dto.auth.AuthResponse;
import com.interviewai.dto.auth.LoginRequest;
import com.interviewai.dto.auth.RegisterRequest;
import com.interviewai.dto.auth.TokenPair;

public interface AuthService {

    AuthResponse register(RegisterRequest request, String userAgent, String ipAddress);

    AuthResponse login(LoginRequest request, String userAgent, String ipAddress);

    TokenPair refresh(String refreshToken, String userAgent, String ipAddress);

    void logout(String refreshToken);
}