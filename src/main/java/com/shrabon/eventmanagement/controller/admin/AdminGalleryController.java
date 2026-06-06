package com.shrabon.eventmanagement.controller.admin;

import com.shrabon.eventmanagement.model.GalleryItem;
import com.shrabon.eventmanagement.service.GalleryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/gallery")
public class AdminGalleryController {

    private final GalleryService galleryService;

    public AdminGalleryController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("active", "gallery");
        model.addAttribute("galleryItems", galleryService.findAll());
        return "admin/gallery/list";
    }

    @PostMapping("/save")
    public String save(@RequestParam String imageUrl,
                       @RequestParam(required = false) String title,
                       @RequestParam(required = false) String category,
                       RedirectAttributes ra) {
        GalleryItem item = new GalleryItem();
        item.setImageUrl(imageUrl);
        item.setTitle(title);
        item.setCategory(category);
        galleryService.save(item);
        ra.addFlashAttribute("success", "Gallery image added.");
        return "redirect:/admin/gallery";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        galleryService.delete(id);
        ra.addFlashAttribute("success", "Gallery image deleted.");
        return "redirect:/admin/gallery";
    }
}
