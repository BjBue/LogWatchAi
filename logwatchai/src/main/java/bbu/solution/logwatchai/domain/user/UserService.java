package bbu.solution.logwatchai.domain.user;

import bbu.solution.logwatchai.domain.user.User.Role;
import bbu.solution.logwatchai.infrastructure.security.JwtResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    User createUser(String username, String plainPassword, Role role);

    JwtResponse authenticate(String username, String plainPassword);

    Optional<User> getUserById(UUID id);

    Optional<User> getUserByUsername(String username);

    User updateUser(User user);

    void disableUser(UUID userId);

    void deleteUser(UUID userId);

    boolean existsByUsername(String username);

    List<User> getAllUsers();
}