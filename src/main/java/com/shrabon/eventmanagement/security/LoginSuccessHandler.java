package com.shrabon.eventmanagement.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Redirects users to their role-specific dashboard after a successful login:
 * <ul>
 *     <li>ADMIN  -&gt; /admin/dashboard</li>
 *     <li>STAFF  -&gt; /staff/dashboard</li>
 *     <li>CLIENT -&gt; /client/dashboard</li>
 * </ul>
 */
@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        String target = "/";
        if (roles.contains("ROLE_ADMIN")) {
            target = "/admin/dashboard";
        } else if (roles.contains("ROLE_STAFF")) {
            target = "/staff/dashboard";
        } else if (roles.contains("ROLE_CLIENT")) {
            target = "/client/dashboard";
        }

        getRedirectStrategy().sendRedirect(request, response, target);
    }
}
