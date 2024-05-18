package com.siemens.backend.domain.repository;

import com.siemens.backend.domain.model.User;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for users.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    /**
     * Check if a particular email exists.
     * @param email - email of a users
     * @return true, if the email exists
     *         false, otherwise
     */
    Boolean existsByEmail(final String email);

    /**
     * Find a user based on their email.
     * @param email - email of a user
     * @return the user having the specified email
     */
    User findByEmail(final String email);
}
