package com.siemens.backend.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class UserFeedback {
    @Id
    @GeneratedValue
    @Column(name = "user_feedback_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
    private String feedback;

    public UserFeedback(final Hotel hotel, final String feedback) {
        this.hotel = hotel;
        this.feedback = feedback;
    }
}
