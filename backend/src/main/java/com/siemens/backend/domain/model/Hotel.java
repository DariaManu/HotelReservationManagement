package com.siemens.backend.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Hotel {
    @Id
    @Column(name = "hotel_id")
    private Long id;
    private String name;
    private double latitude;
    private double longitude;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Room> rooms;

    public Hotel(final Long id, final String name, final double latitude, final double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rooms = new ArrayList<>();
    }
}
