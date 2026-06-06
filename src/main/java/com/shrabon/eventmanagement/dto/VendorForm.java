package com.shrabon.eventmanagement.dto;

import com.shrabon.eventmanagement.model.enums.VendorCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorForm {

    private Long id;

    @NotBlank(message = "Vendor name is required")
    private String name;

    @NotNull(message = "Category is required")
    private VendorCategory category = VendorCategory.OTHER;

    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String serviceDetails;
    private boolean active = true;
}
