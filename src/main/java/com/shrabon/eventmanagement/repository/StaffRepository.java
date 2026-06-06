package com.shrabon.eventmanagement.repository;

import com.shrabon.eventmanagement.model.Staff;
import com.shrabon.eventmanagement.model.enums.StaffAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    Optional<Staff> findByUserId(Long userId);

    Optional<Staff> findByUserEmail(String email);

    List<Staff> findByAvailability(StaffAvailability availability);
}
