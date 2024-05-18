package com.siemens.backend.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO class containing information about reservations made by a user at a specific hotel.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReservationsForHotelAndUserDTO implements Serializable {
    private Long reservationId;
    private String hotelName;
    private Integer roomNumber;
    private String roomType;
    private LocalDate startDate;
    private LocalDate endDate;
}
