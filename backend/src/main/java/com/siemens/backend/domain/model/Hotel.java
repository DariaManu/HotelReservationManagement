package com.siemens.backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a hotel entity.
 */
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
