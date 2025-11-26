package bbu.solution.logwatchai.domain.user;

import bbu.solution.logwatchai.infrastructure.security.JwtResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface defining operations for managing and authenticating users.
 * <p>
 * Implementations of this interface provide user creation, authentication,
 * updating, querying, and deletion functionality. It is the core abstraction
 * used by the application’s security layer to interact with user accounts.
 */
public interface UserService {

    /**
     * Creates a new user with the given username, raw password, and role.
     *
     * @param username      the desired username
     * @param plainPassword the raw password which will be hashed
     * @param role          the assigned user role
     * @return the newly created {@link User}
     */
    User createUser(String username, String plainPassword, Role role);

    /**
     * Authenticates a user using the provided credentials and returns a JWT token response.
     *
     * @param username      the username
     * @param plainPassword the raw password
     * @return a {@link JwtResponse} containing access tokens and authentication details
     */
    JwtResponse authenticate(String username, String plainPassword);

    /**
     * Retrieves a user by its unique ID.
     *
     * @param id the user ID
     * @return an {@link Optional} containing the user if found
     */
    Optional<User> getUserById(UUID id);

    /**
     * Retrieves a user by its unique username.
     *
     * @param username the username to search for
     * @return an {@link Optional} containing the user if found
     */
    Optional<User> getUserByUsername(String username);

    /**
     * Updates an existing user’s data.
     *
     * @param user the user object containing updated values
     * @return the updated {@link User}
     */
    User updateUser(User user);

    /**
     * Disables a user account, preventing future logins.
     *
     * @param userId the ID of the user to disable
     */
    void disableUser(UUID userId);

    /**
     * Permanently deletes a user from the system.
     *
     * @param userId the ID of the user to delete
     */
    void deleteUser(UUID userId);

    /**
     * Checks whether a user with the given username already exists.
     *
     * @param username the username to check
     * @return {@code true} if the username is already taken, otherwise {@code false}
     */
    boolean existsByUsername(String username);

    /**
     * Retrieves all registered users.
     *
     * @return a list of all {@link User} entities
     */
    List<User> getAllUsers();
}
