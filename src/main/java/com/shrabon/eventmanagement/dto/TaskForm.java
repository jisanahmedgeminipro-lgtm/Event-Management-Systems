package com.shrabon.eventmanagement.dto;

import com.shrabon.eventmanagement.model.enums.TaskPriority;
import com.shrabon.eventmanagement.model.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class TaskForm {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private Long bookingId;

    private Long assignedStaffId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deadline;

    private TaskPriority priority = TaskPriority.MEDIUM;

    private TaskStatus status = TaskStatus.PENDING;
}
