package com.shrabon.eventmanagement.repository;

import com.shrabon.eventmanagement.model.Vendor;
import com.shrabon.eventmanagement.model.enums.VendorCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    List<Vendor> findByActiveTrue();

    List<Vendor> findByCategory(VendorCategory category);
}
