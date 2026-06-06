package com.shrabon.eventmanagement.dto;

import com.shrabon.eventmanagement.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Admin form to record a payment / advance against a booking.
 */
@Getter
@Setter
public class PaymentForm {

    @NotNull
    private Long bookingId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method = PaymentMethod.CASH;

    private String referenceNo;

    private String note;
}
