package com.shrabon.eventmanagement.service;

import com.shrabon.eventmanagement.dto.PaymentForm;
import com.shrabon.eventmanagement.model.Payment;

import java.util.List;

public interface PaymentService {

    /** Ensure a payment record exists for a booking (created with booking). */
    Payment getOrCreateForBooking(Long bookingId);

    Payment getByBookingId(Long bookingId);

    Payment getById(Long paymentId);

    List<Payment> findAll();

    /** Record an advance / installment payment and recalculate balances. */
    Payment recordPayment(PaymentForm form);
}
