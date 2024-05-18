package com.siemens.backend.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Request for making a room reservation.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MakeReservationRequest implements Serializable {
    private Long userId;
    private Long hotelId;
    private Long roomId;
    private LocalDate startDate;
    private LocalDate endDate;
}
