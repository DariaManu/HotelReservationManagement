package com.siemens.backend.service;

import com.siemens.backend.domain.model.User;
import com.siemens.backend.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Long login(final String email, final String password) {
        if (!userRepository.existsByEmail(email)) {
            throw new BusinessException("Email does not exists");
        }
        User user = userRepository.findByEmail(email);
        if (!user.getPassword().equals(password)) {
            throw new BusinessException("Password does not match");
        }
        return user.getId();
    }
}
