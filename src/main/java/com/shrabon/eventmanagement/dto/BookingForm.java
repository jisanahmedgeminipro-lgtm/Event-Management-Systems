package com.shrabon.eventmanagement.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Form used by clients to create a booking, optionally based on a package and
 * including chosen custom builder items.
 */
@Getter
@Setter
public class BookingForm {

    private Long packageId;

    @NotNull(message = "Please select an event type")
    private Long categoryId;

    @NotNull(message = "Event date is required")
    @Future(message = "Event date must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate eventDate;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime eventTime;

    @NotBlank(message = "Venue is required")
    private String venue;

    @Positive(message = "Guest count must be greater than zero")
    private int guestCount;

    private String specialRequirements;

    /** IDs of selected customization items (from the custom builder). */
    private List<Long> customizationItemIds = new ArrayList<>();
}
