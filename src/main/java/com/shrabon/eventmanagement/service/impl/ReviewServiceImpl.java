package com.shrabon.eventmanagement.service.impl;

import com.shrabon.eventmanagement.dto.ReviewForm;
import com.shrabon.eventmanagement.exception.ResourceNotFoundException;
import com.shrabon.eventmanagement.model.Booking;
import com.shrabon.eventmanagement.model.Client;
import com.shrabon.eventmanagement.model.Review;
import com.shrabon.eventmanagement.repository.BookingRepository;
import com.shrabon.eventmanagement.repository.ClientRepository;
import com.shrabon.eventmanagement.repository.ReviewRepository;
import com.shrabon.eventmanagement.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ClientRepository clientRepository;
    private final BookingRepository bookingRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             ClientRepository clientRepository,
                             BookingRepository bookingRepository) {
        this.reviewRepository = reviewRepository;
        this.clientRepository = clientRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public Review submit(Long clientUserId, ReviewForm form) {
        Client client = clientRepository.findByUserId(clientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Client profile not found."));
        Review review = new Review();
        review.setClient(client);
        review.setRating(form.getRating());
        review.setComment(form.getComment());
        review.setApproved(false); // requires admin moderation before public display
        if (form.getBookingId() != null) {
            Booking booking = bookingRepository.findById(form.getBookingId()).orElse(null);
            review.setBooking(booking);
        }
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> findApproved() {
        return reviewRepository.findByApprovedTrueOrderByCreatedAtDesc();
    }

    @Override
    public List<Review> findAll() {
        return reviewRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<Review> findByClientUserId(Long clientUserId) {
        Client client = clientRepository.findByUserId(clientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Client profile not found."));
        return reviewRepository.findByClientIdOrderByCreatedAtDesc(client.getId());
    }

    @Override
    @Transactional
    public Review setApproved(Long reviewId, boolean approved) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));
        review.setApproved(approved);
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public double averageRating() {
        return reviewRepository.averageApprovedRating();
    }
}
