package com.shrabon.eventmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Client profile self-service edit form.
 */
@Getter
@Setter
public class ProfileForm {

    @NotBlank(message = "Name is required")
    private String fullName;

    @Size(max = 30)
    private String phone;

    @Size(max = 255)
    private String address;

    @Size(max = 80)
    private String city;

    @Size(max = 40)
    private String nid;

    /** Optional new password; ignored when left blank. */
    private String newPassword;
}
