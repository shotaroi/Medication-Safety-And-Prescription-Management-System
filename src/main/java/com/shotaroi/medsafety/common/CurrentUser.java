package com.shotaroi.medsafety.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Resolves current authenticated user. Falls back to header or default when not authenticated.
 */
public final class CurrentUser {

    private CurrentUser() {}

    public static String get(String headerOverride, String defaultUser) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() != null) {
            return auth.getPrincipal().toString();
        }
        return headerOverride != null && !headerOverride.isBlank() ? headerOverride : defaultUser;
    }
}
