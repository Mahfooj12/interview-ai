package com.interviewai.support;

import com.interviewai.model.User;
import com.interviewai.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        Set<String> roles = Arrays.stream(annotation.roles()).collect(Collectors.toSet());
        User user = User.builder()
                .id(annotation.id())
                .name("Test User")
                .email(annotation.email())
                .passwordHash("irrelevant")
                .roles(roles)
                .status("ACTIVE")
                .build();
        CustomUserDetails details = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                details, null, details.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}