package com.siemens.backend.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Request for adding a feedback for a hotel.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostUserFeedbackRequest implements Serializable {
    private String feedback;
}
