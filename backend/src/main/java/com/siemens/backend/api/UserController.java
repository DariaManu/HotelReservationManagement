package com.siemens.backend.api;

import com.siemens.backend.service.BusinessException;
import com.siemens.backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/login")
@AllArgsConstructor
public class UserController {
    private UserService userService;

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
