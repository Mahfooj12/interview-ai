package com.interviewai.support;

import com.interviewai.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class JwtTestUtils {

    @Autowired
    private JwtService jwtService;

    public String accessToken(String userId, String email, Set<String> roles) {
        return jwtService.generateAccessToken(userId, email, roles);
    }

    public String refreshToken(String userId, String email) {
        return jwtService.generateRefreshToken(userId, email);
    }
}