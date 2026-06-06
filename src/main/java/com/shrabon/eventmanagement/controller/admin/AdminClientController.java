package com.shrabon.eventmanagement.controller.admin;

import com.shrabon.eventmanagement.service.BookingService;
import com.shrabon.eventmanagement.service.ClientService;
import com.shrabon.eventmanagement.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/clients")
public class AdminClientController {

    private final ClientService clientService;
    private final BookingService bookingService;

    public AdminClientController(ClientService clientService,
                                 BookingService bookingService) {
        this.clientService = clientService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String query, Model model) {
        model.addAttribute("active", "clients");
        model.addAttribute("clients", clientService.search(query));
        model.addAttribute("query", query);
        return "admin/clients/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        var client = clientService.getById(id);
        model.addAttribute("active", "clients");
        model.addAttribute("client", client);
        model.addAttribute("bookings", bookingService.findByClientUserId(client.getUser().getId()));
        return "admin/clients/detail";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        clientService.delete(id);
        ra.addFlashAttribute("success", "Client removed.");
        return "redirect:/admin/clients";
    }
}
