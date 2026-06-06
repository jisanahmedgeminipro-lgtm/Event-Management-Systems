package com.shrabon.eventmanagement.controller.client;

import com.shrabon.eventmanagement.dto.*;
import com.shrabon.eventmanagement.exception.BookingConflictException;
import com.shrabon.eventmanagement.model.Booking;
import com.shrabon.eventmanagement.model.Client;
import com.shrabon.eventmanagement.security.SecurityUtils;
import com.shrabon.eventmanagement.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;
    private final PackageService packageService;
    private final EventCategoryService categoryService;
    private final CustomizationService customizationService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final ReviewService reviewService;

    public ClientController(ClientService clientService,
                            PackageService packageService,
                            EventCategoryService categoryService,
                            CustomizationService customizationService,
                            BookingService bookingService,
                            PaymentService paymentService,
                            ReviewService reviewService) {
        this.clientService = clientService;
        this.packageService = packageService;
        this.categoryService = categoryService;
        this.customizationService = customizationService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.reviewService = reviewService;
    }

    private Long userId() {
        return SecurityUtils.currentUserId();
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Long uid = userId();
        Client client = clientService.getByUserId(uid);
        List<Booking> bookings = bookingService.findByClientUserId(uid);
        model.addAttribute("active", "dashboard");
        model.addAttribute("client", client);
        model.addAttribute("bookings", bookings);
        model.addAttribute("totalBookings", bookings.size());
        model.addAttribute("upcoming", bookings.stream()
                .filter(b -> b.getEventDate() != null && !b.getEventDate().isBefore(java.time.LocalDate.now()))
                .count());
        return "client/dashboard";
    }

    // -------------------- Packages --------------------
    @GetMapping("/packages")
    public String packages(Model model) {
        model.addAttribute("active", "packages");
        model.addAttribute("packages", packageService.findActive());
        return "client/packages";
    }

    // -------------------- Custom Package Builder --------------------
    @GetMapping("/customize")
    public String customize(@RequestParam(required = false) Long packageId, Model model) {
        model.addAttribute("active", "customize");
        model.addAttribute("packages", packageService.findActive());
        model.addAttribute("groupedItems", customizationService.findActiveGroupedByType());
        model.addAttribute("categories", categoryService.findActive());
        model.addAttribute("selectedPackageId", packageId);
        if (!model.containsAttribute("bookingForm")) {
            BookingForm form = new BookingForm();
            form.setPackageId(packageId);
            model.addAttribute("bookingForm", form);
        }
        return "client/customize";
    }

    /** AJAX endpoint that returns the live price breakdown for the builder. */
    @PostMapping("/customize/quote")
    @ResponseBody
    public QuoteResult quote(@RequestParam(required = false) Long packageId,
                             @RequestParam(required = false) List<Long> itemIds) {
        return customizationService.calculateQuote(packageId, itemIds);
    }

    // -------------------- Booking --------------------
    @GetMapping("/booking/new")
    public String newBooking(@RequestParam(required = false) Long packageId, Model model) {
        model.addAttribute("active", "customize");
        model.addAttribute("packages", packageService.findActive());
        model.addAttribute("groupedItems", customizationService.findActiveGroupedByType());
        model.addAttribute("categories", categoryService.findActive());
        model.addAttribute("selectedPackageId", packageId);
        if (!model.containsAttribute("bookingForm")) {
            BookingForm form = new BookingForm();
            form.setPackageId(packageId);
            model.addAttribute("bookingForm", form);
        }
        return "client/customize";
    }

    @PostMapping("/booking")
    public String createBooking(@Valid @ModelAttribute("bookingForm") BookingForm form,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("active", "customize");
            model.addAttribute("packages", packageService.findActive());
            model.addAttribute("groupedItems", customizationService.findActiveGroupedByType());
            model.addAttribute("categories", categoryService.findActive());
            model.addAttribute("selectedPackageId", form.getPackageId());
            return "client/customize";
        }
        try {
            Booking booking = bookingService.createBooking(userId(), form);
            ra.addFlashAttribute("success",
                    "Booking created! Your reference is " + booking.getBookingReference() + ".");
            return "redirect:/client/bookings/" + booking.getId();
        } catch (BookingConflictException ex) {
            model.addAttribute("active", "customize");
            model.addAttribute("packages", packageService.findActive());
            model.addAttribute("groupedItems", customizationService.findActiveGroupedByType());
            model.addAttribute("categories", categoryService.findActive());
            model.addAttribute("selectedPackageId", form.getPackageId());
            model.addAttribute("error", ex.getMessage());
            return "client/customize";
        }
    }

    @GetMapping("/bookings")
    public String bookings(Model model) {
        model.addAttribute("active", "bookings");
        model.addAttribute("bookings", bookingService.findByClientUserId(userId()));
        return "client/bookings";
    }

    @GetMapping("/bookings/{id}")
    public String bookingDetail(@PathVariable Long id, Model model) {
        Booking booking = bookingService.getById(id);
        // Ownership guard
        if (!booking.getClient().getUser().getId().equals(userId())) {
            return "redirect:/client/bookings";
        }
        model.addAttribute("active", "bookings");
        model.addAttribute("booking", booking);
        model.addAttribute("payment", paymentService.getOrCreateForBooking(id));
        return "client/booking-detail";
    }

    // -------------------- Payments --------------------
    @GetMapping("/payments")
    public String payments(Model model) {
        model.addAttribute("active", "payments");
        model.addAttribute("bookings", bookingService.findByClientUserId(userId()));
        return "client/payments";
    }

    // -------------------- Reviews --------------------
    @GetMapping("/reviews")
    public String reviews(Model model) {
        model.addAttribute("active", "reviews");
        model.addAttribute("reviews", reviewService.findByClientUserId(userId()));
        model.addAttribute("bookings", bookingService.findByClientUserId(userId()));
        if (!model.containsAttribute("reviewForm")) {
            model.addAttribute("reviewForm", new ReviewForm());
        }
        return "client/reviews";
    }

    @PostMapping("/reviews")
    public String submitReview(@Valid @ModelAttribute("reviewForm") ReviewForm form,
                               BindingResult bindingResult,
                               RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "Please provide a valid rating.");
            return "redirect:/client/reviews";
        }
        reviewService.submit(userId(), form);
        ra.addFlashAttribute("success", "Thank you! Your review will appear after moderation.");
        return "redirect:/client/reviews";
    }

    // -------------------- Profile --------------------
    @GetMapping("/profile")
    public String profile(Model model) {
        Client client = clientService.getByUserId(userId());
        if (!model.containsAttribute("profileForm")) {
            ProfileForm form = new ProfileForm();
            form.setFullName(client.getUser().getFullName());
            form.setPhone(client.getUser().getPhone());
            form.setAddress(client.getAddress());
            form.setCity(client.getCity());
            form.setNid(client.getNid());
            model.addAttribute("profileForm", form);
        }
        model.addAttribute("active", "profile");
        model.addAttribute("client", client);
        return "client/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileForm") ProfileForm form,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("active", "profile");
            model.addAttribute("client", clientService.getByUserId(userId()));
            return "client/profile";
        }
        clientService.updateProfile(userId(), form);
        ra.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/client/profile";
    }
}
