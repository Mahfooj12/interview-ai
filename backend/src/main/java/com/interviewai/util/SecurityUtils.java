package com.interviewai.util;

import com.interviewai.exception.UnauthorizedException;
import com.interviewai.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static CustomUserDetails currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails details)) {
            throw new UnauthorizedException("No authenticated user");
        }
        return details;
    }

    public static String currentUserId() {
        return currentUser().getId();
    }

    public static boolean isAdmin() {
        return currentUser().getRoles().contains(Constants.ROLE_ADMIN);
    }
}
