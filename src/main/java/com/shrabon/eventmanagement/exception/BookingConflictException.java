package com.shrabon.eventmanagement.exception;

/**
 * Raised when a booking cannot be created/confirmed because the venue is
 * already reserved for the requested date (double-booking prevention).
 */
public class BookingConflictException extends RuntimeException {
    public BookingConflictException(String message) {
        super(message);
    }
}
