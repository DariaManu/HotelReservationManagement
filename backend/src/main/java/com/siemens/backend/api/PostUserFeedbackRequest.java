package com.siemens.backend.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostUserFeedbackRequest implements Serializable {
    private String feedback;
}
