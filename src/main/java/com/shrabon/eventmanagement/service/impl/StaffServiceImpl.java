package com.shrabon.eventmanagement.service.impl;

import com.shrabon.eventmanagement.dto.StaffForm;
import com.shrabon.eventmanagement.exception.ResourceNotFoundException;
import com.shrabon.eventmanagement.model.Staff;
import com.shrabon.eventmanagement.model.User;
import com.shrabon.eventmanagement.model.enums.Role;
import com.shrabon.eventmanagement.repository.StaffRepository;
import com.shrabon.eventmanagement.repository.UserRepository;
import com.shrabon.eventmanagement.service.StaffService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StaffServiceImpl(StaffRepository staffRepository,
                            UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Staff> findAll() {
        return staffRepository.findAll();
    }

    @Override
    public Staff getById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + id));
    }

    @Override
    public Staff getByUserId(Long userId) {
        return staffRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff profile not found for user: " + userId));
    }

    @Override
    public Staff getByEmail(String email) {
        return staffRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + email));
    }

    @Override
    @Transactional
    public Staff save(StaffForm form) {
        Staff staff;
        User user;
        if (form.getId() != null) {
            staff = getById(form.getId());
            user = staff.getUser();
        } else {
            if (userRepository.existsByEmail(form.getEmail())) {
                throw new IllegalArgumentException("Email already in use: " + form.getEmail());
            }
            staff = new Staff();
            user = new User();
            user.setRole(Role.STAFF);
            user.setEnabled(true);
            staff.setJoinDate(LocalDate.now());
        }

        user.setFullName(form.getFullName());
        user.setEmail(form.getEmail().toLowerCase().trim());
        user.setPhone(form.getPhone());
        if (StringUtils.hasText(form.getPassword())) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        } else if (form.getId() == null) {
            // default temporary password for newly created staff
            user.setPassword(passwordEncoder.encode("Staff@12345"));
        }
        user = userRepository.save(user);

        staff.setUser(user);
        staff.setPosition(form.getPosition());
        staff.setSalary(form.getSalary());
        staff.setSkills(form.getSkills());
        staff.setAvailability(form.getAvailability());
        return staffRepository.save(staff);
    }

    @Override
    public StaffForm toForm(Staff staff) {
        StaffForm form = new StaffForm();
        form.setId(staff.getId());
        form.setFullName(staff.getUser().getFullName());
        form.setEmail(staff.getUser().getEmail());
        form.setPhone(staff.getUser().getPhone());
        form.setPosition(staff.getPosition());
        form.setSalary(staff.getSalary());
        form.setSkills(staff.getSkills());
        form.setAvailability(staff.getAvailability());
        return form;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Staff staff = getById(id);
        User user = staff.getUser();
        staffRepository.delete(staff);
        userRepository.delete(user);
    }

    @Override
    public long count() {
        return staffRepository.count();
    }
}
