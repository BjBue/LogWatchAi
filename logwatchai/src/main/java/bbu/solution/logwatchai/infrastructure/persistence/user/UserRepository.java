package bbu.solution.logwatchai.infrastructure.persistence.user;

import bbu.solution.logwatchai.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing User entities in the database.
 * Provides standard CRUD operations and queries for username-based lookups.
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the User if found, empty otherwise
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks whether a user with the given username exists.
     *
     * @param username the username to check
     * @return true if a user with the username exists, false otherwise
     */
    boolean existsByUsername(String username);
}
