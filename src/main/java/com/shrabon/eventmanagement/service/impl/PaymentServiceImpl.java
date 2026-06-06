package com.shrabon.eventmanagement.service.impl;

import com.shrabon.eventmanagement.dto.PaymentForm;
import com.shrabon.eventmanagement.exception.ResourceNotFoundException;
import com.shrabon.eventmanagement.model.Booking;
import com.shrabon.eventmanagement.model.Payment;
import com.shrabon.eventmanagement.model.PaymentTransaction;
import com.shrabon.eventmanagement.repository.BookingRepository;
import com.shrabon.eventmanagement.repository.PaymentRepository;
import com.shrabon.eventmanagement.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public Payment getOrCreateForBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId).orElseGet(() -> {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setTotalAmount(booking.getTotalAmount());
            payment.setPaidAmount(BigDecimal.ZERO);
            payment.recalculate();
            return paymentRepository.save(payment);
        });
    }

    @Override
    public Payment getByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking: " + bookingId));
    }

    @Override
    public Payment getById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    @Transactional
    public Payment recordPayment(PaymentForm form) {
        Payment payment = getOrCreateForBooking(form.getBookingId());

        // Keep total in sync with the (possibly updated) booking total.
        payment.setTotalAmount(payment.getBooking().getTotalAmount());

        PaymentTransaction txn = new PaymentTransaction();
        txn.setAmount(form.getAmount());
        txn.setMethod(form.getMethod());
        txn.setReferenceNo(form.getReferenceNo());
        txn.setNote(form.getNote());
        txn.setPaidAt(LocalDateTime.now());
        payment.addTransaction(txn);

        payment.setPaidAmount(payment.getPaidAmount().add(form.getAmount()));
        payment.recalculate();
        return paymentRepository.save(payment);
    }
}
