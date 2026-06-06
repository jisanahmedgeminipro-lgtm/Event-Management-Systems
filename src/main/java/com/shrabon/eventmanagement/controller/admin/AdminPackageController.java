package com.shrabon.eventmanagement.controller.admin;

import com.shrabon.eventmanagement.dto.PackageForm;
import com.shrabon.eventmanagement.service.EventCategoryService;
import com.shrabon.eventmanagement.service.PackageService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/packages")
public class AdminPackageController {

    private final PackageService packageService;
    private final EventCategoryService categoryService;

    public AdminPackageController(PackageService packageService,
                                 EventCategoryService categoryService) {
        this.packageService = packageService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("active", "packages");
        model.addAttribute("packages", packageService.findAll());
        return "admin/packages/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("active", "packages");
        if (!model.containsAttribute("packageForm")) {
            model.addAttribute("packageForm", new PackageForm());
        }
        model.addAttribute("categories", categoryService.findAll());
        return "admin/packages/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("active", "packages");
        model.addAttribute("packageForm", packageService.toForm(packageService.getById(id)));
        model.addAttribute("categories", categoryService.findAll());
        return "admin/packages/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("packageForm") PackageForm form,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("active", "packages");
            model.addAttribute("categories", categoryService.findAll());
            return "admin/packages/form";
        }
        packageService.save(form);
        ra.addFlashAttribute("success", "Package saved successfully.");
        return "redirect:/admin/packages";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        packageService.delete(id);
        ra.addFlashAttribute("success", "Package deleted.");
        return "redirect:/admin/packages";
    }
}
