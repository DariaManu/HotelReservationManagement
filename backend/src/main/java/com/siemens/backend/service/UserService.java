package com.siemens.backend.service;

import com.siemens.backend.domain.model.User;
import com.siemens.backend.domain.repository.UserRepository;
import com.siemens.backend.service.exception.BusinessException;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * Service class containing the business logic for users.
 */
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * Login a user. Throws an exception if the credentials are incorrect.
     * @param email - email of the user
     * @param password - password of the user
     * @return id of the user
     * @throws BusinessException if the given email does not exist
     * @throws BusinessException if the password does not match with the password associated with the email
     */
    public Long login(final String email, final String password) {
        if (!userRepository.existsByEmail(email)) {
            throw new BusinessException("Email does not exist");
        }
        User user = userRepository.findByEmail(email);
        if (!user.getPassword().equals(password)) {
            throw new BusinessException("Password does not match");
        }
        return user.getId();
    }
}
