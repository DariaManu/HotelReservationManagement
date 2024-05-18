package com.siemens.backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Class representing a room of a hotel.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Embeddable
public class Room {
    @Id
    @GeneratedValue
    @Column(name = "room_id")
    private Long id;
    private Integer roomNumber;
    private RoomType type;
    private Integer price;

}
