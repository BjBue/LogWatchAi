package bbu.solution.logwatchai.application.user;

import bbu.solution.logwatchai.domain.user.Role;
import bbu.solution.logwatchai.domain.user.User;
import bbu.solution.logwatchai.domain.user.UserService;
import bbu.solution.logwatchai.infrastructure.persistence.user.UserRepository;
import bbu.solution.logwatchai.infrastructure.security.JwtResponse;
import bbu.solution.logwatchai.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link UserService} interface that manages users,
 * authentication, and security-related operations.
 *
 * <p>I handle user creation, JWT authentication, user state updates,
 * and interaction with the underlying {@link UserRepository}. Passwords are
 * always written using the configured {@link PasswordEncoder}, and I use
 * {@link JwtService} to generate authentication tokens.</p>
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    /**
     * Creates a new instance of the service with all required dependencies.
     *
     * @param repo the repository used to persist and load user entities
     * @param encoder the encoder used to hash and verify passwords
     * @param jwtService the component used to create JWT tokens
     */
    public UserServiceImpl(UserRepository repo,
                           PasswordEncoder encoder,
                           JwtService jwtService) {
        this.userRepository = repo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    /**
     * Creates a new user with the given credentials and role.
     *
     * <p>I verify that the username does not already exist. If it does, I
     * throw an exception. Otherwise I hash the plain-text password and store
     * the new user in the database.</p>
     *
     * @param username the user's login name
     * @param plainPassword the raw password provided by the client
     * @param role the assigned user role
     * @return the created {@link User}
     * @throws RuntimeException if the username already exists
     */
    @Override
    public User createUser(String username, String plainPassword, Role role) {
        if (userRepository.existsByUsername(username))
            throw new RuntimeException("Username exists");

        User u = new User(username, encoder.encode(plainPassword), role);
        return userRepository.save(u);
    }

    /**
     * Authenticates the user and issues a signed JWT token.
     *
     * <p>I load the user by username, verify the provided password, and
     * generate a signed JWT containing user identity and role. I also update
     * the user's last-login timestamp before returning the token as
     * {@link JwtResponse}.</p>
     *
     * @param username the supplied username
     * @param plainPassword the supplied raw password
     * @return a full {@link JwtResponse} including expiration metadata
     * @throws RuntimeException if the user does not exist or the password is invalid
     */
    @Override
    public JwtResponse authenticate(String username, String plainPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(plainPassword, user.getPasswordHash()))
            throw new RuntimeException("Invalid password");

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        Instant expires = Instant.now().plusSeconds(3600);

        user.updateLastLogin();
        userRepository.save(user);

        return new JwtResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                token,
                "Bearer",
                expires
        );
    }

    /**
     * Returns a user by its identifier.
     *
     * @param id the user ID
     * @return an {@link Optional} containing the user if present
     */
    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    /**
     * Returns a user by its username.
     *
     * @param username the username to search for
     * @return an {@link Optional} containing the user if found
     */
    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Updates and persists an existing user entity.
     *
     * <p>I simply save the given user instance. The caller is responsible
     * for providing a valid and already modified entity.</p>
     *
     * @param user the updated user entity
     * @return the persisted user
     */
    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Marks a user as disabled.
     *
     * <p>I load the user by ID and update the `enabled` flag. If the user does
     * not exist, the method performs no action.</p>
     *
     * @param userId the user ID to disable
     */
    @Override
    public void disableUser(UUID userId) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setEnabled(false);
            userRepository.save(u);
        });
    }

    /**
     * Deletes a user by its identifier.
     *
     * @param userId the ID of the user to remove
     */
    @Override
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }

    /**
     * Checks if a username already exists.
     *
     * @param username the username to test
     * @return true if the username is already taken, false otherwise
     */
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Returns all users stored in the system.
     *
     * @return a list containing all {@link User} entities
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
