package com.shrabon.eventmanagement.controller;

import com.shrabon.eventmanagement.dto.RegisterForm;
import com.shrabon.eventmanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login() {
        return "public/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "public/access-denied";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        return "public/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm registerForm,
                           BindingResult bindingResult,
                           RedirectAttributes ra) {
        if (authService.emailExists(registerForm.getEmail())) {
            bindingResult.rejectValue("email", "exists", "An account with this email already exists.");
        }
        if (!registerForm.isPasswordMatching()) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "Passwords do not match.");
        }
        if (bindingResult.hasErrors()) {
            return "public/register";
        }
        authService.registerClient(registerForm);
        ra.addFlashAttribute("success", "Registration successful. Please sign in.");
        return "redirect:/login";
    }
}
