package com.siemens.backend.domain.repository;

import com.siemens.backend.domain.model.Room;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for rooms.
 */
@Repository
public interface RoomRepository extends CrudRepository<Room, Long> {
}
