package com.shrabon.eventmanagement.service;

import com.shrabon.eventmanagement.dto.ReviewForm;
import com.shrabon.eventmanagement.model.Review;

import java.util.List;

public interface ReviewService {

    Review submit(Long clientUserId, ReviewForm form);

    List<Review> findApproved();

    List<Review> findAll();

    List<Review> findByClientUserId(Long clientUserId);

    Review setApproved(Long reviewId, boolean approved);

    void delete(Long id);

    double averageRating();
}
