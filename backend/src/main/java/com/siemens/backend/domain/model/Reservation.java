package com.siemens.backend.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
