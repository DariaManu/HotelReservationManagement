package com.siemens.backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Class representing a reservation made by a user, for a room which belongs to
 * a certain hotel, during a period of time.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Reservation {
    @Id
    @GeneratedValue
    @Column(name = "reservation_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
    private LocalDate startDate;
    private LocalDate endDate;

    public Reservation(final User user, final Hotel hotel, final Room room, final LocalDate startDate, final LocalDate endDate) {
        this.user = user;
        this.hotel = hotel;
        this.room = room;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
