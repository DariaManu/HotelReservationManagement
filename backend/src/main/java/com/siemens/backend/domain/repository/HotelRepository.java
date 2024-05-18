package com.siemens.backend.domain.repository;

import com.siemens.backend.domain.model.Hotel;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for hotels.
 */
@Repository
public interface HotelRepository extends CrudRepository<Hotel, Long> {
}
