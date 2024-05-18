package com.siemens.backend.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Request for logging in a user.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginRequest implements Serializable {
    private String email;
    private String password;
}
