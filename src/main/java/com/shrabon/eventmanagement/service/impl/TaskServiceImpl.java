package com.shrabon.eventmanagement.service.impl;

import com.shrabon.eventmanagement.dto.TaskForm;
import com.shrabon.eventmanagement.exception.ResourceNotFoundException;
import com.shrabon.eventmanagement.model.Booking;
import com.shrabon.eventmanagement.model.Staff;
import com.shrabon.eventmanagement.model.Task;
import com.shrabon.eventmanagement.model.enums.TaskStatus;
import com.shrabon.eventmanagement.repository.BookingRepository;
import com.shrabon.eventmanagement.repository.StaffRepository;
import com.shrabon.eventmanagement.repository.TaskRepository;
import com.shrabon.eventmanagement.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final StaffRepository staffRepository;
    private final BookingRepository bookingRepository;

    public TaskServiceImpl(TaskRepository taskRepository,
                           StaffRepository staffRepository,
                           BookingRepository bookingRepository) {
        this.taskRepository = taskRepository;
        this.staffRepository = staffRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public Task getById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    }

    @Override
    @Transactional
    public Task save(TaskForm form) {
        Task task = form.getId() != null ? getById(form.getId()) : new Task();
        task.setTitle(form.getTitle());
        task.setDescription(form.getDescription());
        task.setDeadline(form.getDeadline());
        task.setPriority(form.getPriority());
        task.setStatus(form.getStatus());

        if (form.getAssignedStaffId() != null) {
            Staff staff = staffRepository.findById(form.getAssignedStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff not found."));
            task.setAssignedStaff(staff);
        } else {
            task.setAssignedStaff(null);
        }

        if (form.getBookingId() != null) {
            Booking booking = bookingRepository.findById(form.getBookingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
            task.setBooking(booking);
        } else {
            task.setBooking(null);
        }
        return taskRepository.save(task);
    }

    @Override
    public TaskForm toForm(Task task) {
        TaskForm form = new TaskForm();
        form.setId(task.getId());
        form.setTitle(task.getTitle());
        form.setDescription(task.getDescription());
        form.setDeadline(task.getDeadline());
        form.setPriority(task.getPriority());
        form.setStatus(task.getStatus());
        form.setAssignedStaffId(task.getAssignedStaff() != null ? task.getAssignedStaff().getId() : null);
        form.setBookingId(task.getBooking() != null ? task.getBooking().getId() : null);
        return form;
    }

    @Override
    public List<Task> findByStaffUserId(Long staffUserId) {
        Staff staff = staffRepository.findByUserId(staffUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff profile not found."));
        return taskRepository.findByAssignedStaffIdOrderByDeadlineAsc(staff.getId());
    }

    @Override
    @Transactional
    public Task updateStatus(Long taskId, TaskStatus status) {
        Task task = getById(taskId);
        task.setStatus(status);
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public long countByStaffUserIdAndStatus(Long staffUserId, TaskStatus status) {
        Staff staff = staffRepository.findByUserId(staffUserId).orElse(null);
        if (staff == null) {
            return 0;
        }
        return taskRepository.countByAssignedStaffIdAndStatus(staff.getId(), status);
    }
}
