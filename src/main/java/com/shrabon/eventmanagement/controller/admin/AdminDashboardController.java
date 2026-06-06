package com.shrabon.eventmanagement.controller.admin;

import com.shrabon.eventmanagement.service.BookingService;
import com.shrabon.eventmanagement.service.DashboardService;
import com.shrabon.eventmanagement.service.StaffService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final DashboardService dashboardService;
    private final BookingService bookingService;
    private final StaffService staffService;

    public AdminDashboardController(DashboardService dashboardService,
                                    BookingService bookingService,
                                    StaffService staffService) {
        this.dashboardService = dashboardService;
        this.bookingService = bookingService;
        this.staffService = staffService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("active", "dashboard");
        model.addAttribute("stats", dashboardService.getAdminStats());
        model.addAttribute("upcomingEvents", bookingService.findUpcoming());
        model.addAttribute("recentBookings", bookingService.findAll().stream().limit(8).toList());
        model.addAttribute("staffList", staffService.findAll());
        return "admin/dashboard";
    }
}
