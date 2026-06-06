package com.shrabon.eventmanagement.dto;

import com.shrabon.eventmanagement.model.enums.CustomizationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CustomizationItemForm {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Type is required")
    private CustomizationType type = CustomizationType.DECORATION;

    private String description;

    @NotNull
    @PositiveOrZero
    private BigDecimal price = BigDecimal.ZERO;

    private String unit = "package";

    private boolean active = true;
}
