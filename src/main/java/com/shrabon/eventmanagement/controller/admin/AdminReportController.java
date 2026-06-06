package com.shrabon.eventmanagement.controller.admin;

import com.shrabon.eventmanagement.service.BookingService;
import com.shrabon.eventmanagement.service.ClientService;
import com.shrabon.eventmanagement.service.DashboardService;
import com.shrabon.eventmanagement.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/reports")
public class AdminReportController {

    private final DashboardService dashboardService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final ClientService clientService;

    public AdminReportController(DashboardService dashboardService,
                                 BookingService bookingService,
                                 PaymentService paymentService,
                                 ClientService clientService) {
        this.dashboardService = dashboardService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.clientService = clientService;
    }

    @GetMapping
    public String reports(Model model) {
        model.addAttribute("active", "reports");
        model.addAttribute("stats", dashboardService.getAdminStats());
        model.addAttribute("bookings", bookingService.findAll());
        model.addAttribute("payments", paymentService.findAll());
        model.addAttribute("clients", clientService.findAll());
        return "admin/reports/index";
    }
}
