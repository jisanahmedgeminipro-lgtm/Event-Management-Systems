package com.shrabon.eventmanagement.controller.admin;

import com.shrabon.eventmanagement.service.ContactService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/messages")
public class AdminContactController {

    private final ContactService contactService;

    public AdminContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("active", "messages");
        model.addAttribute("messages", contactService.findAll());
        return "admin/messages/list";
    }

    @PostMapping("/{id}/handled")
    public String markHandled(@PathVariable Long id, RedirectAttributes ra) {
        contactService.markHandled(id);
        ra.addFlashAttribute("success", "Message marked as handled.");
        return "redirect:/admin/messages";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        contactService.delete(id);
        ra.addFlashAttribute("success", "Message deleted.");
        return "redirect:/admin/messages";
    }
}
