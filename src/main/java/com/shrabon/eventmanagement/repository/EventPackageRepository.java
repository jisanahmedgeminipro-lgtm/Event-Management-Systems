package com.shrabon.eventmanagement.repository;

import com.shrabon.eventmanagement.model.EventPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventPackageRepository extends JpaRepository<EventPackage, Long> {

    List<EventPackage> findByActiveTrue();

    List<EventPackage> findByActiveTrueAndFeaturedTrue();

    List<EventPackage> findByCategoryId(Long categoryId);
}
