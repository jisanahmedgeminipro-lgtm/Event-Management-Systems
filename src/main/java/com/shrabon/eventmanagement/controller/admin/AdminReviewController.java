package com.shrabon.eventmanagement.controller.admin;

import com.shrabon.eventmanagement.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("active", "reviews");
        model.addAttribute("reviews", reviewService.findAll());
        return "admin/reviews/list";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes ra) {
        reviewService.setApproved(id, true);
        ra.addFlashAttribute("success", "Review approved and published.");
        return "redirect:/admin/reviews";
    }

    @PostMapping("/{id}/unapprove")
    public String unapprove(@PathVariable Long id, RedirectAttributes ra) {
        reviewService.setApproved(id, false);
        ra.addFlashAttribute("success", "Review hidden from the website.");
        return "redirect:/admin/reviews";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        reviewService.delete(id);
        ra.addFlashAttribute("success", "Review deleted.");
        return "redirect:/admin/reviews";
    }
}
