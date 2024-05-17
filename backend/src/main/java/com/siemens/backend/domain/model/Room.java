package com.siemens.backend.domain.model;

import jakarta.persistence.*;
import lombok.*;

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