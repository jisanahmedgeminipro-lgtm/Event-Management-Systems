package com.shrabon.eventmanagement.service.impl;

import com.shrabon.eventmanagement.dto.DashboardStats;
import com.shrabon.eventmanagement.model.enums.BookingStatus;
import com.shrabon.eventmanagement.model.enums.Role;
import com.shrabon.eventmanagement.repository.*;
import com.shrabon.eventmanagement.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final EventPackageRepository packageRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final ReviewRepository reviewRepository;

    public DashboardServiceImpl(BookingRepository bookingRepository,
                                UserRepository userRepository,
                                StaffRepository staffRepository,
                                EventPackageRepository packageRepository,
                                ContactMessageRepository contactMessageRepository,
                                ReviewRepository reviewRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
        this.packageRepository = packageRepository;
        this.contactMessageRepository = contactMessageRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public DashboardStats getAdminStats() {
        DashboardStats stats = new DashboardStats();
        stats.setTotalBookings(bookingRepository.count());
        stats.setPendingBookings(bookingRepository.countByStatus(BookingStatus.PENDING));
        stats.setConfirmedBookings(bookingRepository.countByStatus(BookingStatus.CONFIRMED));
        stats.setCompletedBookings(bookingRepository.countByStatus(BookingStatus.COMPLETED));
        stats.setTotalClients(userRepository.countByRole(Role.CLIENT));
        stats.setTotalStaff(staffRepository.count());
        stats.setTotalPackages(packageRepository.count());
        stats.setUpcomingEvents(bookingRepository.findUpcoming(java.time.LocalDate.now()).size());
        stats.setUnreadMessages(contactMessageRepository.countByHandledFalse());

        BigDecimal contracted = bookingRepository.sumContractedValue();
        BigDecimal collected = bookingRepository.sumCollectedRevenue();
        BigDecimal pending = bookingRepository.sumPendingPayments();
        stats.setTotalRevenue(contracted != null ? contracted : BigDecimal.ZERO);
        stats.setCollectedRevenue(collected != null ? collected : BigDecimal.ZERO);
        stats.setPendingPayments(pending != null ? pending : BigDecimal.ZERO);
        stats.setAverageRating(reviewRepository.averageApprovedRating());
        return stats;
    }
}
