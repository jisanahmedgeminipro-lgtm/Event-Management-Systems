package com.shrabon.eventmanagement.controller.staff;

import com.shrabon.eventmanagement.model.Staff;
import com.shrabon.eventmanagement.model.enums.TaskStatus;
import com.shrabon.eventmanagement.security.SecurityUtils;
import com.shrabon.eventmanagement.service.BookingService;
import com.shrabon.eventmanagement.service.StaffService;
import com.shrabon.eventmanagement.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final StaffService staffService;
    private final BookingService bookingService;
    private final TaskService taskService;

    public StaffController(StaffService staffService,
                           BookingService bookingService,
                           TaskService taskService) {
        this.staffService = staffService;
        this.bookingService = bookingService;
        this.taskService = taskService;
    }

    private Long userId() {
        return SecurityUtils.currentUserId();
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Long uid = userId();
        Staff staff = staffService.getByUserId(uid);
        model.addAttribute("active", "dashboard");
        model.addAttribute("staff", staff);
        model.addAttribute("events", bookingService.findByStaffUserId(uid));
        model.addAttribute("tasks", taskService.findByStaffUserId(uid));
        model.addAttribute("pendingTasks", taskService.countByStaffUserIdAndStatus(uid, TaskStatus.PENDING));
        model.addAttribute("inProgressTasks", taskService.countByStaffUserIdAndStatus(uid, TaskStatus.IN_PROGRESS));
        model.addAttribute("completedTasks", taskService.countByStaffUserIdAndStatus(uid, TaskStatus.COMPLETED));
        return "staff/dashboard";
    }

    @GetMapping("/events")
    public String events(Model model) {
        model.addAttribute("active", "events");
        model.addAttribute("events", bookingService.findByStaffUserId(userId()));
        return "staff/events";
    }

    @GetMapping("/events/{id}")
    public String eventDetail(@PathVariable Long id, Model model) {
        model.addAttribute("active", "events");
        model.addAttribute("booking", bookingService.getById(id));
        return "staff/event-detail";
    }

    @GetMapping("/tasks")
    public String tasks(Model model) {
        model.addAttribute("active", "tasks");
        model.addAttribute("tasks", taskService.findByStaffUserId(userId()));
        model.addAttribute("statuses", TaskStatus.values());
        return "staff/tasks";
    }

    @PostMapping("/tasks/{id}/status")
    public String updateTaskStatus(@PathVariable Long id,
                                   @RequestParam TaskStatus status,
                                   RedirectAttributes ra) {
        taskService.updateStatus(id, status);
        ra.addFlashAttribute("success", "Task status updated.");
        return "redirect:/staff/tasks";
    }

    @GetMapping("/schedule")
    public String schedule(Model model) {
        model.addAttribute("active", "schedule");
        model.addAttribute("events", bookingService.findByStaffUserId(userId()));
        return "staff/schedule";
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        Long uid = userId();
        model.addAttribute("active", "notifications");
        model.addAttribute("tasks", taskService.findByStaffUserId(uid));
        model.addAttribute("events", bookingService.findByStaffUserId(uid));
        return "staff/notifications";
    }
}
