package com.shrabon.eventmanagement.controller.admin;

import com.shrabon.eventmanagement.model.enums.BookingStatus;
import com.shrabon.eventmanagement.model.Booking;
import com.shrabon.eventmanagement.service.BookingService;
import com.shrabon.eventmanagement.service.EmailService;
import com.shrabon.eventmanagement.service.PaymentService;
import com.shrabon.eventmanagement.service.StaffService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    private final BookingService bookingService;
    private final StaffService staffService;
    private final PaymentService paymentService;
    private final EmailService emailService;

    public AdminBookingController(BookingService bookingService,
                                  StaffService staffService,
                                  PaymentService paymentService,
                                  EmailService emailService) {
        this.bookingService = bookingService;
        this.staffService = staffService;
        this.paymentService = paymentService;
        this.emailService = emailService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("active", "bookings");
        model.addAttribute("bookings", bookingService.findAll());
        model.addAttribute("statuses", BookingStatus.values());
        return "admin/bookings/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("active", "bookings");
        model.addAttribute("booking", bookingService.getById(id));
        model.addAttribute("statuses", BookingStatus.values());
        model.addAttribute("allStaff", staffService.findAll());
        model.addAttribute("payment", paymentService.getOrCreateForBooking(id));
        return "admin/bookings/detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam BookingStatus status,
                               RedirectAttributes ra) {
        Booking booking = bookingService.updateStatus(id, status);
        emailService.bookingStatus(booking);
        ra.addFlashAttribute("success", "Booking status updated to " + status.getLabel() + ". The client has been notified by email.");
        return "redirect:/admin/bookings/" + id;
    }

    @PostMapping("/{id}/assign")
    public String assignStaff(@PathVariable Long id,
                              @RequestParam(required = false) Set<Long> staffIds,
                              RedirectAttributes ra) {
        Booking booking = bookingService.assignStaff(id, staffIds);
        emailService.staffAssignment(booking.getAssignedStaff(), booking);
        ra.addFlashAttribute("success", "Assigned staff updated. Assigned members have been notified by email.");
        return "redirect:/admin/bookings/" + id;
    }
}
