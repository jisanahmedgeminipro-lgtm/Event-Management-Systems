package com.shrabon.eventmanagement.controller.admin;

import com.shrabon.eventmanagement.dto.CustomizationItemForm;
import com.shrabon.eventmanagement.model.enums.CustomizationType;
import com.shrabon.eventmanagement.service.CustomizationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Admin maintains the catalogue of customization items used by the
 * Custom Package Builder.
 */
@Controller
@RequestMapping("/admin/customizations")
public class AdminCustomizationController {

    private final CustomizationService customizationService;

    public AdminCustomizationController(CustomizationService customizationService) {
        this.customizationService = customizationService;
    }

    @GetMapping
    public String list(Model model) {
        if (!model.containsAttribute("itemForm")) {
            model.addAttribute("itemForm", new CustomizationItemForm());
        }
        return "admin/customizations/list";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        var item = customizationService.getById(id);
        CustomizationItemForm form = new CustomizationItemForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setType(item.getType());
        form.setDescription(item.getDescription());
        form.setPrice(item.getPrice());
        form.setUnit(item.getUnit());
        form.setActive(item.isActive());
        model.addAttribute("active", "customizations");
        model.addAttribute("items", customizationService.findAll());
        model.addAttribute("types", CustomizationType.values());
        model.addAttribute("itemForm", form);
        return "admin/customizations/list";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("itemForm") CustomizationItemForm form,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("active", "customizations");
            model.addAttribute("items", customizationService.findAll());
            model.addAttribute("types", CustomizationType.values());
            return "admin/customizations/list";
        }
        customizationService.save(form);
        ra.addFlashAttribute("success", "Customization item saved.");
        return "redirect:/admin/customizations";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        customizationService.delete(id);
        ra.addFlashAttribute("success", "Customization item deleted.");
        return "redirect:/admin/customizations";
    }
}
