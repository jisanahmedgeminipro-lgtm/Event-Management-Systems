package com.shrabon.eventmanagement.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Convenience helpers for retrieving the currently authenticated user.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static CustomUserDetails currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails details) {
            return details;
        }
        return null;
    }

    public static Long currentUserId() {
        CustomUserDetails details = currentUser();
        return details != null ? details.getId() : null;
    }

    public static String currentEmail() {
        CustomUserDetails details = currentUser();
        return details != null ? details.getUsername() : null;
    }
}
