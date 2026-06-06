package com.shrabon.eventmanagement.repository;

import com.shrabon.eventmanagement.model.Task;
import com.shrabon.eventmanagement.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedStaffIdOrderByDeadlineAsc(Long staffId);

    List<Task> findByAssignedStaffIdAndStatus(Long staffId, TaskStatus status);

    List<Task> findAllByOrderByCreatedAtDesc();

    long countByAssignedStaffIdAndStatus(Long staffId, TaskStatus status);
}
