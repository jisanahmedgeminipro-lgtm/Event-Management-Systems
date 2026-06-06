package com.shrabon.eventmanagement.repository;

import com.shrabon.eventmanagement.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByApprovedTrueOrderByCreatedAtDesc();

    List<Review> findByClientIdOrderByCreatedAtDesc(Long clientId);

    List<Review> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.approved = true")
    double averageApprovedRating();

    long countByApprovedTrue();
}
