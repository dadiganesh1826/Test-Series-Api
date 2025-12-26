package com.testseries.repository;

import com.testseries.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByLastLoginDateAfter(java.time.LocalDateTime date);
    long countByLastActivityDateAfter(java.time.LocalDateTime date);
}
