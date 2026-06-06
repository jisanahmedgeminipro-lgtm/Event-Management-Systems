package com.shrabon.eventmanagement.controller.admin;

import com.shrabon.eventmanagement.model.enums.BookingStatus;
import com.shrabon.eventmanagement.service.BookingService;
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

    public AdminBookingController(BookingService bookingService,
                                  StaffService staffService,
                                  PaymentService paymentService) {
        this.bookingService = bookingService;
        this.staffService = staffService;
        this.paymentService = paymentService;
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
        bookingService.updateStatus(id, status);
        ra.addFlashAttribute("success", "Booking status updated to " + status.getLabel() + ".");
        return "redirect:/admin/bookings/" + id;
    }

    @PostMapping("/{id}/assign")
    public String assignStaff(@PathVariable Long id,
                              @RequestParam(required = false) Set<Long> staffIds,
                              RedirectAttributes ra) {
        bookingService.assignStaff(id, staffIds);
        ra.addFlashAttribute("success", "Assigned staff updated.");
        return "redirect:/admin/bookings/" + id;
    }
}
