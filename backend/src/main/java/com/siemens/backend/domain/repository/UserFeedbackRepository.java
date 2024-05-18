package com.siemens.backend.domain.repository;

import com.siemens.backend.domain.model.Hotel;

import com.siemens.backend.domain.model.UserFeedback;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for user feedback.
 */
@Repository
public interface UserFeedbackRepository extends CrudRepository<UserFeedback, Long> {
    /**
     * Get all feedback given to a particular hotel.
     * @param hotel - the hotel the feedback was given to
     * @return list of feedback given to the specified hotel
     */
    List<UserFeedback> getAllByHotel(final Hotel hotel);
}
