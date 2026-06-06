package com.shrabon.eventmanagement.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Aggregated KPI values for the admin dashboard.
 */
@Getter
@Setter
public class DashboardStats {

    private long totalBookings;
    private long pendingBookings;
    private long confirmedBookings;
    private long completedBookings;
    private long totalClients;
    private long totalStaff;
    private long totalPackages;
    private long upcomingEvents;
    private long unreadMessages;
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private BigDecimal collectedRevenue = BigDecimal.ZERO;
    private BigDecimal pendingPayments = BigDecimal.ZERO;
    private double averageRating;
}
