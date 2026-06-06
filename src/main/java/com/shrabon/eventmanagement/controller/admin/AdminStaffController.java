package com.shrabon.eventmanagement.controller.admin;

import com.shrabon.eventmanagement.dto.StaffForm;
import com.shrabon.eventmanagement.model.enums.StaffAvailability;
import com.shrabon.eventmanagement.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/staff")
public class AdminStaffController {

    private final StaffService staffService;

    public AdminStaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("active", "staff");
        model.addAttribute("staffList", staffService.findAll());
        return "admin/staff/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("active", "staff");
        if (!model.containsAttribute("staffForm")) {
            model.addAttribute("staffForm", new StaffForm());
        }
        model.addAttribute("availabilities", StaffAvailability.values());
        return "admin/staff/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("active", "staff");
        model.addAttribute("staffForm", staffService.toForm(staffService.getById(id)));
        model.addAttribute("availabilities", StaffAvailability.values());
        return "admin/staff/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("staffForm") StaffForm form,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("active", "staff");
            model.addAttribute("availabilities", StaffAvailability.values());
            return "admin/staff/form";
        }
        try {
            staffService.save(form);
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("email", "exists", ex.getMessage());
            model.addAttribute("active", "staff");
            model.addAttribute("availabilities", StaffAvailability.values());
            return "admin/staff/form";
        }
        ra.addFlashAttribute("success", "Staff member saved. Default password (new staff): Staff@12345");
        return "redirect:/admin/staff";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        staffService.delete(id);
        ra.addFlashAttribute("success", "Staff member removed.");
        return "redirect:/admin/staff";
    }
}
