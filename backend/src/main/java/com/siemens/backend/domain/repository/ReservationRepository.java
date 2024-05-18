package com.siemens.backend.domain.repository;

import com.siemens.backend.domain.model.Hotel;
import com.siemens.backend.domain.model.Reservation;
import com.siemens.backend.domain.model.Room;
import com.siemens.backend.domain.model.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for reservations.
 */
@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {

    /**
     * Get all reservations for a particular room, which are made during a time period that overlaps the interval
     * of dates given by startDate and endDate.
     * @param roomId - id of the room that a user wants to reserve
     * @param startDate - starting date of a possible new reservation
     * @param endDate - ending date of a possible new reservation
     * @return list of reservations which overlap the interval of dates specified by startDate and endDate
     */
    @Query("SELECT r FROM Reservation r WHERE r.id= :roomId " +
            "AND ((r.startDate <= :startDate AND r.endDate > :startDate) " +
            "OR (r.startDate < :endDate AND r.endDate >= :endDate) " +
            "OR (r.startDate > :startDate AND r.endDate < :endDate))")
    List<Reservation> getOverlappingReservations(final @Param("roomId") Long roomId,
                                                           final @Param("startDate") LocalDate startDate,
                                                           final @Param("endDate") LocalDate endDate);

    /**
     * Get all reservations made by a user for a hotel.
     * @param hotel - the hotel for which the reservations were made
     * @param user - the user which made the reservations
     * @return list of reservations made by the specified user for the specified hotel
     */
    List<Reservation> getAllByHotelAndUser(final Hotel hotel, final User user);

    /**
     * Get all reservations made for a particular room.
     * @param room - the room for which the reservations were made
     * @return list of reservations which contain the specified room
     */
    List<Reservation> getAllByRoom(final Room room);
}
