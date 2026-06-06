package com.shrabon.eventmanagement.repository;

import com.shrabon.eventmanagement.model.Booking;
import com.shrabon.eventmanagement.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingReference(String bookingReference);

    List<Booking> findByClientIdOrderByCreatedAtDesc(Long clientId);

    List<Booking> findByStatusOrderByEventDateAsc(BookingStatus status);

    List<Booking> findAllByOrderByCreatedAtDesc();

    long countByStatus(BookingStatus status);

    /**
     * Double-booking detection: an active (non-cancelled) booking already exists
     * for the same venue on the same date. Optionally excludes a booking id
     * (used when editing an existing booking).
     */
    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.eventDate = :eventDate
              AND LOWER(TRIM(b.venue)) = LOWER(TRIM(:venue))
              AND b.status <> com.shrabon.eventmanagement.model.enums.BookingStatus.CANCELLED
              AND (:excludeId IS NULL OR b.id <> :excludeId)
            """)
    boolean existsVenueConflict(@Param("venue") String venue,
                                @Param("eventDate") LocalDate eventDate,
                                @Param("excludeId") Long excludeId);

    /** Upcoming non-cancelled events from today onward. */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.eventDate >= :fromDate
              AND b.status <> com.shrabon.eventmanagement.model.enums.BookingStatus.CANCELLED
            ORDER BY b.eventDate ASC
            """)
    List<Booking> findUpcoming(@Param("fromDate") LocalDate fromDate);

    /** Bookings assigned to a given staff member (via booking_staff join). */
    @Query("SELECT b FROM Booking b JOIN b.assignedStaff s WHERE s.id = :staffId ORDER BY b.eventDate ASC")
    List<Booking> findByAssignedStaffId(@Param("staffId") Long staffId);

    /** Total confirmed/collected revenue (sum of paid amounts). */
    @Query("SELECT COALESCE(SUM(p.paidAmount), 0) FROM Payment p")
    BigDecimal sumCollectedRevenue();

    @Query("SELECT COALESCE(SUM(p.dueAmount), 0) FROM Payment p")
    BigDecimal sumPendingPayments();

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.status <> com.shrabon.eventmanagement.model.enums.BookingStatus.CANCELLED")
    BigDecimal sumContractedValue();

    List<Booking> findByEventDateBetweenOrderByEventDateAsc(LocalDate start, LocalDate end);
}
