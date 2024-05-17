package com.siemens.backend.domain.repository;

import com.siemens.backend.domain.model.Reservation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.id= :roomId AND ((r.startDate <= :startDate AND r.endDate > :startDate) OR (r.startDate < :endDate AND r.endDate >= :endDate) OR (r.startDate > :startDate AND r.endDate < :endDate))")
    List<Reservation> getOverlappingReservations(final @Param("roomId") Long roomId,
                                                           final @Param("startDate") LocalDate startDate,
                                                           final @Param("endDate") LocalDate endDate);
}
