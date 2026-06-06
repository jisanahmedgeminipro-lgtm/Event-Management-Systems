package com.shrabon.eventmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Public self-registration form (creates a CLIENT account).
 */
@Getter
@Setter
public class RegisterForm {

    @NotBlank(message = "Full name is required")
    @Size(max = 120)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(max = 30)
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    @Size(max = 255)
    private String address;

    @Size(max = 80)
    private String city;

    @Size(max = 40)
    private String nid;

    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
}
