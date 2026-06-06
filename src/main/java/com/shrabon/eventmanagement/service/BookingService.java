package com.shrabon.eventmanagement.service;

import com.shrabon.eventmanagement.dto.BookingForm;
import com.shrabon.eventmanagement.model.Booking;
import com.shrabon.eventmanagement.model.enums.BookingStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface BookingService {

    /** Create a booking for the given client, applying double-booking prevention. */
    Booking createBooking(Long clientUserId, BookingForm form);

    Booking getById(Long id);

    Booking getByReference(String reference);

    List<Booking> findAll();

    List<Booking> findByClientUserId(Long clientUserId);

    List<Booking> findByStaffUserId(Long staffUserId);

    List<Booking> findUpcoming();

    Booking updateStatus(Long bookingId, BookingStatus status);

    Booking assignStaff(Long bookingId, Set<Long> staffIds);

    /** True when the venue is free on the given date (no active conflict). */
    boolean isVenueAvailable(String venue, LocalDate date, Long excludeBookingId);

    long countByStatus(BookingStatus status);
}
