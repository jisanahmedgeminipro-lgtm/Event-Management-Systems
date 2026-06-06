package com.shrabon.eventmanagement.service;

import com.shrabon.eventmanagement.dto.TaskForm;
import com.shrabon.eventmanagement.model.Task;
import com.shrabon.eventmanagement.model.enums.TaskStatus;

import java.util.List;

public interface TaskService {

    List<Task> findAll();

    Task getById(Long id);

    Task save(TaskForm form);

    TaskForm toForm(Task task);

    List<Task> findByStaffUserId(Long staffUserId);

    Task updateStatus(Long taskId, TaskStatus status);

    void delete(Long id);

    long countByStaffUserIdAndStatus(Long staffUserId, TaskStatus status);
}
