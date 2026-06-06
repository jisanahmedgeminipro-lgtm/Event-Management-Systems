package com.shrabon.eventmanagement.repository;

import com.shrabon.eventmanagement.model.GalleryItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryItemRepository extends JpaRepository<GalleryItem, Long> {

    List<GalleryItem> findAllByOrderByCreatedAtDesc();

    List<GalleryItem> findByOrderByCreatedAtDesc(Pageable pageable);
}
