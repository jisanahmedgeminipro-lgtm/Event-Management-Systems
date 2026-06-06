package com.shrabon.eventmanagement.controller;

import com.shrabon.eventmanagement.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.Year;

/**
 * Injects values that every Thymeleaf template can rely on.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("companyName")
    public String companyName() {
        return "Shrabon Decorator & Event Management";
    }

    @ModelAttribute("companyShortName")
    public String companyShortName() {
        return "Shrabon Events";
    }

    @ModelAttribute("ownerName")
    public String ownerName() {
        return "Md. Tara Mia";
    }

    @ModelAttribute("currentYear")
    public int currentYear() {
        return Year.now().getValue();
    }

    @ModelAttribute("currentUserName")
    public String currentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails details) {
            return details.getFullName();
        }
        return null;
    }
}
