package com.siemens.backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class representing the feedback given by a user to a specific hotel.
 */
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
