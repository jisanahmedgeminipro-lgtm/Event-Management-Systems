package com.shrabon.eventmanagement.controller.admin;

import com.shrabon.eventmanagement.dto.TaskForm;
import com.shrabon.eventmanagement.model.Task;
import com.shrabon.eventmanagement.model.enums.TaskPriority;
import com.shrabon.eventmanagement.model.enums.TaskStatus;
import com.shrabon.eventmanagement.service.BookingService;
import com.shrabon.eventmanagement.service.EmailService;
import com.shrabon.eventmanagement.service.StaffService;
import com.shrabon.eventmanagement.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/tasks")
public class AdminTaskController {

    private final TaskService taskService;
    private final StaffService staffService;
    private final BookingService bookingService;
    private final EmailService emailService;

    public AdminTaskController(TaskService taskService,
                               StaffService staffService,
                               BookingService bookingService,
                               EmailService emailService) {
        this.taskService = taskService;
        this.staffService = staffService;
        this.bookingService = bookingService;
        this.emailService = emailService;
    }

    private void populate(Model model) {
        model.addAttribute("active", "staff");
        model.addAttribute("tasks", taskService.findAll());
        model.addAttribute("staffList", staffService.findAll());
        model.addAttribute("bookings", bookingService.findAll());
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("statuses", TaskStatus.values());
    }

    @GetMapping
    public String list(Model model) {
        populate(model);
        if (!model.containsAttribute("taskForm")) {
            model.addAttribute("taskForm", new TaskForm());
        }
        return "admin/tasks/list";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        populate(model);
        model.addAttribute("taskForm", taskService.toForm(taskService.getById(id)));
        return "admin/tasks/list";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("taskForm") TaskForm form,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            populate(model);
            return "admin/tasks/list";
        }
        Task saved = taskService.save(form);
        emailService.taskAssignment(saved);
        ra.addFlashAttribute("success", "Task saved." + (saved.getAssignedStaff() != null ? " Assigned staff notified by email." : ""));
        return "redirect:/admin/tasks";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        taskService.delete(id);
        ra.addFlashAttribute("success", "Task deleted.");
        return "redirect:/admin/tasks";
    }
}
