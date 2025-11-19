package bbu.solution.logwatchai.infrastructure.security;

import bbu.solution.logwatchai.domain.user.Role;
import bbu.solution.logwatchai.domain.user.User;
import bbu.solution.logwatchai.domain.user.UserService;
import bbu.solution.logwatchai.infrastructure.persistence.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public UserServiceImpl(UserRepository repo,
                           PasswordEncoder encoder,
                           JwtService jwtService) {
        this.userRepository = repo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @Override
    public User createUser(String username, String plainPassword, Role role) {
        if (userRepository.existsByUsername(username))
            throw new RuntimeException("Username exists");

        User u = new User(username, encoder.encode(plainPassword), role);
        return userRepository.save(u);
    }

    @Override
    public JwtResponse authenticate(String username, String plainPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(plainPassword, user.getPasswordHash()))
            throw new RuntimeException("Invalid password");

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        user.updateLastLogin();
        userRepository.save(user);

        //return new JwtResponse(token);
        Instant expires = Instant.now().plusSeconds(3600);
        return new JwtResponse(user.getId(), user.getUsername(), user.getRole(), token, "Bearer",expires);
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void disableUser(UUID userId) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setEnabled(false);
            userRepository.save(u);
        });
    }

    @Override
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
