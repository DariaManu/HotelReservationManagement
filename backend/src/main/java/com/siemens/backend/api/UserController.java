package com.siemens.backend.api;

import com.siemens.backend.api.request.LoginRequest;
import com.siemens.backend.service.exception.BusinessException;
import com.siemens.backend.service.UserService;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for users.
 */
@RestController
@CrossOrigin
@RequestMapping("/login")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    /**
     * Log in a user.
     * @param request - request containing user credentials.
     * @return ResponseEntity.ok() if the user was successfully logged in
     *         ResponseEntity.status(HttpStatus.BAD_REQUEST) if a business error occurred
     */
    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Long userId;
        try {
            userId = userService.login(request.getEmail(), request.getPassword());
        } catch (BusinessException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        return ResponseEntity.ok().body(userId);
    }
}
