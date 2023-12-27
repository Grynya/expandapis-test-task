package com.expandapis.testtask.repository;

import com.expandapis.testtask.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    boolean existsUserByUsername(String username);

    Optional<AppUser> findByUsername(String username);
}