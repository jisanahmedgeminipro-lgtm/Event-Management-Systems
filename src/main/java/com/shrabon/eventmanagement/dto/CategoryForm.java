package com.shrabon.eventmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryForm {

    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    private String icon;

    private boolean active = true;
}
