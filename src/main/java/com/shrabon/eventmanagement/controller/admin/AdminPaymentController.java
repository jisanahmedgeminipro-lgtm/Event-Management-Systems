package com.shrabon.eventmanagement.controller.admin;

import com.shrabon.eventmanagement.dto.PaymentForm;
import com.shrabon.eventmanagement.model.enums.PaymentMethod;
import com.shrabon.eventmanagement.service.BookingService;
import com.shrabon.eventmanagement.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/payments")
public class AdminPaymentController {

    private final PaymentService paymentService;
    private final BookingService bookingService;

    public AdminPaymentController(PaymentService paymentService,
                                  BookingService bookingService) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("active", "payments");
        model.addAttribute("payments", paymentService.findAll());
        return "admin/payments/list";
    }

    @GetMapping("/booking/{bookingId}")
    public String forBooking(@PathVariable Long bookingId, Model model) {
        model.addAttribute("active", "payments");
        model.addAttribute("payment", paymentService.getOrCreateForBooking(bookingId));
        model.addAttribute("booking", bookingService.getById(bookingId));
        model.addAttribute("methods", PaymentMethod.values());
        PaymentForm form = new PaymentForm();
        form.setBookingId(bookingId);
        model.addAttribute("paymentForm", form);
        return "admin/payments/detail";
    }

    @PostMapping("/record")
    public String record(@Valid @ModelAttribute("paymentForm") PaymentForm form,
                         BindingResult bindingResult,
                         RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "Please enter a valid payment amount.");
            return "redirect:/admin/payments/booking/" + form.getBookingId();
        }
        paymentService.recordPayment(form);
        ra.addFlashAttribute("success", "Payment recorded successfully.");
        return "redirect:/admin/payments/booking/" + form.getBookingId();
    }

    @GetMapping("/invoice/{bookingId}")
    public String invoice(@PathVariable Long bookingId, Model model) {
        model.addAttribute("booking", bookingService.getById(bookingId));
        model.addAttribute("payment", paymentService.getOrCreateForBooking(bookingId));
        return "admin/payments/invoice";
    }
}
