package com.siemens.backend.domain.repository;

import com.siemens.backend.domain.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Boolean existsByEmail(final String email);
    User findByEmail(final String email);
}
