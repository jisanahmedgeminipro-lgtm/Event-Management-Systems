package com.shrabon.eventmanagement.dto;

import com.shrabon.eventmanagement.model.enums.StaffAvailability;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Admin form to create / edit a STAFF member (creates the underlying user too).
 */
@Getter
@Setter
public class StaffForm {

    private Long id;

    @NotBlank(message = "Name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @Size(max = 30)
    private String phone;

    /** Required only when creating a new staff member. */
    private String password;

    @NotBlank(message = "Position is required")
    private String position;

    private BigDecimal salary = BigDecimal.ZERO;

    private String skills;

    private StaffAvailability availability = StaffAvailability.AVAILABLE;
}
