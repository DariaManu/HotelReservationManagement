package com.siemens.backend.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginRequest implements Serializable {
    private String email;
    private String password;
}
