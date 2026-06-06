package com.shrabon.eventmanagement.controller.admin;

import com.shrabon.eventmanagement.dto.CategoryForm;
import com.shrabon.eventmanagement.service.EventCategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Event Management — admin maintains the catalogue of event types/categories.
 */
@Controller
@RequestMapping("/admin/events")
public class AdminEventCategoryController {

    private final EventCategoryService categoryService;

    public AdminEventCategoryController(EventCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("active", "events");
        model.addAttribute("categories", categoryService.findAll());
        if (!model.containsAttribute("categoryForm")) {
            model.addAttribute("categoryForm", new CategoryForm());
        }
        return "admin/events/list";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        var category = categoryService.getById(id);
        CategoryForm form = new CategoryForm();
        form.setId(category.getId());
        form.setName(category.getName());
        form.setDescription(category.getDescription());
        form.setIcon(category.getIcon());
        form.setActive(category.isActive());
        model.addAttribute("active", "events");
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("categoryForm", form);
        return "admin/events/list";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("categoryForm") CategoryForm form,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("active", "events");
            model.addAttribute("categories", categoryService.findAll());
            return "admin/events/list";
        }
        categoryService.save(form);
        ra.addFlashAttribute("success", "Event type saved.");
        return "redirect:/admin/events";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        categoryService.delete(id);
        ra.addFlashAttribute("success", "Event type deleted.");
        return "redirect:/admin/events";
    }
}
