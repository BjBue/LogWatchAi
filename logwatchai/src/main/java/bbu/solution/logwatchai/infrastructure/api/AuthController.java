package bbu.solution.logwatchai.infrastructure.api;

import bbu.solution.logwatchai.domain.user.User;
import bbu.solution.logwatchai.domain.user.UserService;
import bbu.solution.logwatchai.infrastructure.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller handling authentication-related operations such as user login.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserService userService;

    /**
     * Creates an AuthController with required authentication components.
     *
     * @param authManager Spring Security authentication manager
     * @param jwtService service responsible for JWT generation
     * @param userService service for retrieving user information
     */
    public AuthController(AuthenticationManager authManager, JwtService jwtService,  UserService userService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    /**
     * Authenticates a user with username and password and returns a JWT token.
     *
     * @param request login credentials (username, password)
     * @return LoginResponse containing username, role, JWT token, and token type
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        Authentication notUsedAuth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = userService.getUserByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("username not found"));

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        return new LoginResponse(
                user.getUsername(),
                user.getRole().name(),
                token,
                "Bearer"
        );
    }

    /**
     * Request payload for login.
     *
     * @param username user's username
     * @param password user's password
     */
    public record LoginRequest(String username, String password) {}

    /**
     * Response payload for successful authentication.
     *
     * @param username authenticated user's username
     * @param role authenticated user's role
     * @param token generated JWT token
     * @param type token type (usually "Bearer")
     */
    public record LoginResponse(String username, String role, String token, String type) {}

}
