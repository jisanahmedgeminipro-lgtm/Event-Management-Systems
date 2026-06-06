package com.shrabon.eventmanagement.service;

import com.shrabon.eventmanagement.dto.StaffForm;
import com.shrabon.eventmanagement.model.Staff;

import java.util.List;

public interface StaffService {

    List<Staff> findAll();

    Staff getById(Long id);

    Staff getByUserId(Long userId);

    Staff getByEmail(String email);

    Staff save(StaffForm form);

    StaffForm toForm(Staff staff);

    void delete(Long id);

    long count();
}
