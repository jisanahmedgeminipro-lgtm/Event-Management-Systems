package com.shrabon.eventmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Admin form to create / edit an event package.
 */
@Getter
@Setter
public class PackageForm {

    private Long id;

    @NotBlank(message = "Package name is required")
    private String name;

    private Long categoryId;

    private String description;

    @NotNull(message = "Base price is required")
    @PositiveOrZero(message = "Base price cannot be negative")
    private BigDecimal basePrice = BigDecimal.ZERO;

    @PositiveOrZero(message = "Discount cannot be negative")
    private BigDecimal discountPercent = BigDecimal.ZERO;

    private boolean decoration;
    private boolean catering;
    private boolean photography;
    private boolean videography;
    private boolean lighting;
    private boolean soundSystem;
    private boolean stageSetup;

    @PositiveOrZero
    private int guestCapacity;

    private boolean featured;
    private boolean active = true;

    /** Newline-separated features entered in a textarea. */
    private String featuresText;

    /** Newline-separated image URLs. */
    private String imageUrls;
}
