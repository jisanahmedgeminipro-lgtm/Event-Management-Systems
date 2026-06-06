package com.shrabon.eventmanagement.controller.admin;

import com.shrabon.eventmanagement.dto.VendorForm;
import com.shrabon.eventmanagement.model.enums.VendorCategory;
import com.shrabon.eventmanagement.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/vendors")
public class AdminVendorController {

    private final VendorService vendorService;

    public AdminVendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("active", "vendors");
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("categories", VendorCategory.values());
        if (!model.containsAttribute("vendorForm")) {
            model.addAttribute("vendorForm", new VendorForm());
        }
        return "admin/vendors/list";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        var v = vendorService.getById(id);
        VendorForm form = new VendorForm();
        form.setId(v.getId());
        form.setName(v.getName());
        form.setCategory(v.getCategory());
        form.setContactPerson(v.getContactPerson());
        form.setPhone(v.getPhone());
        form.setEmail(v.getEmail());
        form.setAddress(v.getAddress());
        form.setServiceDetails(v.getServiceDetails());
        form.setActive(v.isActive());
        model.addAttribute("active", "vendors");
        model.addAttribute("vendors", vendorService.findAll());
        model.addAttribute("categories", VendorCategory.values());
        model.addAttribute("vendorForm", form);
        return "admin/vendors/list";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("vendorForm") VendorForm form,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("active", "vendors");
            model.addAttribute("vendors", vendorService.findAll());
            model.addAttribute("categories", VendorCategory.values());
            return "admin/vendors/list";
        }
        vendorService.save(form);
        ra.addFlashAttribute("success", "Vendor saved.");
        return "redirect:/admin/vendors";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        vendorService.delete(id);
        ra.addFlashAttribute("success", "Vendor deleted.");
        return "redirect:/admin/vendors";
    }
}
