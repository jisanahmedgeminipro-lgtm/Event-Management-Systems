package com.shrabon.eventmanagement.controller.admin;

import com.shrabon.eventmanagement.service.WebsiteContentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * Website Content Management — edit key/value content blocks shown on the
 * public site (hero text, about, mission, vision, contact info, etc.).
 */
@Controller
@RequestMapping("/admin/content")
public class AdminContentController {

    private final WebsiteContentService contentService;

    public AdminContentController(WebsiteContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping
    public String edit(Model model) {
        model.addAttribute("active", "content");
        model.addAttribute("content", contentService.asMap());
        return "admin/content/edit";
    }

    @PostMapping("/save")
    public String save(@RequestParam Map<String, String> params, RedirectAttributes ra) {
        params.forEach((key, value) -> {
            if (key.startsWith("content_")) {
                contentService.save(key.substring("content_".length()), value);
            }
        });
        ra.addFlashAttribute("success", "Website content updated.");
        return "redirect:/admin/content";
    }
}
