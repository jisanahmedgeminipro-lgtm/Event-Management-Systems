package com.shrabon.eventmanagement.controller;
import com.shrabon.eventmanagement.service.EmailService;

import com.shrabon.eventmanagement.dto.ContactForm;
import com.shrabon.eventmanagement.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Serves the public-facing business website.
 */
@Controller
public class PublicController {

    private final PackageService packageService;
    private final EventCategoryService categoryService;
    private final GalleryService galleryService;
    private final ReviewService reviewService;
    private final BookingService bookingService;
    private final ClientService clientService;
    private final StaffService staffService;
    private final ContactService contactService;
    private final EmailService emailService;

    public PublicController(PackageService packageService,
                            EventCategoryService categoryService,
                            GalleryService galleryService,
                            ReviewService reviewService,
                            BookingService bookingService,
                            ClientService clientService,
                            StaffService staffService,
                            ContactService contactService,
                            EmailService emailService) {
        this.packageService = packageService;
        this.categoryService = categoryService;
        this.galleryService = galleryService;
        this.reviewService = reviewService;
        this.bookingService = bookingService;
        this.clientService = clientService;
        this.staffService = staffService;
        this.contactService = contactService;
        this.emailService = emailService;
    }


    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("featuredPackages", packageService.findFeatured());
        model.addAttribute("categories", categoryService.findActive());
        model.addAttribute("reviews", reviewService.findApproved());
        model.addAttribute("galleryItems", galleryService.findLatest(8));
        model.addAttribute("upcomingEvents", bookingService.findUpcoming());
        // Statistics
        model.addAttribute("statBookings", bookingService.findAll().size());
        model.addAttribute("statClients", clientService.count());
        model.addAttribute("statStaff", staffService.count());
        model.addAttribute("statPackages", packageService.count());
        model.addAttribute("avgRating", reviewService.averageRating());
        return "public/home";
    }

    @GetMapping("/about")
    public String about() {
        return "public/about";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("categories", categoryService.findActive());
        return "public/services";
    }

    @GetMapping("/packages")
    public String packages(Model model) {
        model.addAttribute("packages", packageService.findActive());
        model.addAttribute("categories", categoryService.findActive());
        return "public/packages";
    }

    @GetMapping("/packages/{id}")
    public String packageDetail(@PathVariable Long id, Model model) {
        model.addAttribute("pkg", packageService.getById(id));
        return "public/package-detail";
    }

    @GetMapping("/gallery")
    public String gallery(Model model) {
        model.addAttribute("galleryItems", galleryService.findAll());
        return "public/gallery";
    }

    @GetMapping("/reviews")
    public String reviews(Model model) {
        model.addAttribute("reviews", reviewService.findApproved());
        model.addAttribute("avgRating", reviewService.averageRating());
        return "public/reviews";
    }

    /** "Booking" nav entry — security forwards anonymous users to the login page. */
    @GetMapping("/booking")
    public String booking(@RequestParam(required = false) Long packageId) {
        if (packageId != null) {
            return "redirect:/client/booking/new?packageId=" + packageId;
        }
        return "redirect:/client/booking/new";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        if (!model.containsAttribute("contactForm")) {
            model.addAttribute("contactForm", new ContactForm());
        }
        return "public/contact";
    }

    @PostMapping("/contact")
    public String submitContact(@Valid @ModelAttribute("contactForm") ContactForm contactForm,
                                BindingResult bindingResult,
                                RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "public/contact";
        }
        var saved = contactService.save(contactForm);
        emailService.contactAcknowledgement(saved);
        ra.addFlashAttribute("success", "Thank you! Your message has been sent. A confirmation email is on its way.");
        return "redirect:/contact";
    }
}
