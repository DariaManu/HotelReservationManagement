package com.siemens.backend.domain.repository;

import com.siemens.backend.domain.model.Hotel;
import com.siemens.backend.domain.model.UserFeedback;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFeedbackRepository extends CrudRepository<UserFeedback, Long> {
    List<UserFeedback> getAllByHotel(final Hotel hotel);
}
