package com.shrabon.eventmanagement.repository;

import com.shrabon.eventmanagement.model.CustomizationItem;
import com.shrabon.eventmanagement.model.enums.CustomizationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomizationItemRepository extends JpaRepository<CustomizationItem, Long> {

    List<CustomizationItem> findByActiveTrue();

    List<CustomizationItem> findByTypeAndActiveTrue(CustomizationType type);
}
